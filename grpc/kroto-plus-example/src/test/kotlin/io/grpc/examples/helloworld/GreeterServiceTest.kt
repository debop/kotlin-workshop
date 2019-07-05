@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.grpc.examples.helloworld

import com.github.marcoferrer.krotoplus.coroutines.launchProducerJob
import com.github.marcoferrer.krotoplus.coroutines.withCoroutineContext
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.examples.helloword.GreeterCoroutineGrpc
import io.grpc.examples.helloword.send
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.consumeEachIndexed
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.util.concurrent.ForkJoinPool

/**
 * GreeterServiceTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 4
 */
@ObsoleteCoroutinesApi
class GreeterServiceTest {

    companion object: KLogging()

    val server: Server = InProcessServerBuilder
        .forName("helloworld")
        .addService(GreeterService())
        .directExecutor()
        .build()
        .start()

    val channel: ManagedChannel = InProcessChannelBuilder
        .forName("helloworld")
        .executor(ForkJoinPool.commonPool())  // NOTE: 서버랑 같이 directExecutor()를 사용하면 가끔 먹통이 된다. 이를 방지하기 위해 이 것을 사용한다
        .build()

    val stub: GreeterCoroutineGrpc.GreeterCoroutineStub = GreeterCoroutineGrpc.newStub(channel)

    @AfterAll
    fun cleanup() {
        channel.shutdown()
        server.shutdown()
    }

    @Test
    fun `call unary by coroutines`() = runBlocking<Unit> {
        val client = stub.withCoroutineContext(Dispatchers.IO)

        val response = client.sayHello { name = "John" }

        logger.debug { "Unary response=${response.message}" }
        response.shouldNotBeNull()
        response.message shouldEqual "Hello there, John!"

    }

    @RepeatedTest(5)
    fun `call server streaming`() = runBlocking<Unit> {
        val client = stub.withCoroutineContext(Dispatchers.IO)

        val responseChannel = client.sayHelloServerStreaming { name = "Sunghyouk Bae" }

        responseChannel.consumeEach {
            logger.debug { "Server Streaming response=${it.message}" }
            it.message.shouldNotBeEmpty()
        }
    }

    @RepeatedTest(5)
    fun `call client streaming`() = runBlocking<Unit> {
        val client = stub.withCoroutineContext(Dispatchers.IO)

        val (requestChannel, response) = client.sayHelloClientStreaming()

        launchProducerJob(requestChannel) {
            repeat(10) {
                send { name = "person #$it" }
            }
        }
        val reply = response.await()
        logger.debug { "Client streaming response=${reply.toString().trim()}" }
        reply.shouldNotBeNull()
        reply.message.shouldNotBeEmpty()
    }

    @RepeatedTest(10)
    fun `call bidirectional streaming`() = runBlocking<Unit> {

        val client = GreeterCoroutineGrpc.newStub(channel).withCoroutineContext()
        val (requestChannel, responseChannel) = client.sayHelloStreaming()

        requestChannel.invokeOnClose { cause ->
            logger.debug { "request channel is closed. cause=$cause" }
        }

        val requestCount = 1000
        val requestJob = launchProducerJob(requestChannel) {
            repeat(requestCount) {
                //                logger.trace { "-> Send request #$it" }
                send { name = "person #$it" }
            }
        }

        val responseCount = async(Dispatchers.IO) {
            var responseCount = 0
            responseChannel.consumeEachIndexed { (index, response) ->
                responseCount = index + 1
                //logger.trace { "<- Response: ${response.message}" }
            }
            responseCount
        }

        delay(10)

        requestJob.join()
        responseCount.await() shouldEqualTo requestCount
        coroutineContext.cancelChildren()
    }
}