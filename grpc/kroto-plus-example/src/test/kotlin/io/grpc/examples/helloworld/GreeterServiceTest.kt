@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.grpc.examples.helloworld

import com.github.marcoferrer.krotoplus.coroutines.launchProducerJob
import com.github.marcoferrer.krotoplus.coroutines.withCoroutineContext
import io.grpc.examples.helloword.GreeterCoroutineGrpc
import io.grpc.examples.helloword.send
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.consumeEachIndexed
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

/**
 * GreeterServiceTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 4
 */
@ObsoleteCoroutinesApi
class GreeterServiceTest {

    companion object: KLogging()

    val server = InProcessServerBuilder
        .forName("helloworld")
        .addService(GreeterService())
        .directExecutor()
        .build()
        .start()

    val channel = InProcessChannelBuilder
        .forName("helloworld")
        .directExecutor()
        .build()

    val stub = GreeterCoroutineGrpc.newStub(channel)

    @Test
    fun `call unary by coroutines`() = runBlocking<Unit> {
        val client = stub.withCoroutineContext(Dispatchers.IO)

        val response = client.sayHello { name = "John" }

        logger.debug { "Unary response=${response.message}" }
        response.shouldNotBeNull()
        response.message shouldEqual "Hello there, John!"
    }

    @RepeatedTest(10)
    fun `call server streaming`() = runBlocking<Unit> {
        val client = stub.withCoroutineContext(Dispatchers.IO)

        val responseChannel = client.sayHelloServerStreaming { name = "John" }

        responseChannel.consumeEach {
            logger.debug { "Server Streaming response=${it.message}" }
            it.message.shouldNotBeEmpty()
        }
    }

    @RepeatedTest(10)
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

        val client = stub.withCoroutineContext(Dispatchers.IO)

        val (requestChannel, responseChannel) = client.sayHelloStreaming()

        requestChannel.invokeOnClose { cause ->
            logger.debug { "request channel is closed." }
        }

        val requestCount = 300
        val requestJob = launchProducerJob(requestChannel, Dispatchers.IO) {
            repeat(requestCount) {
                send { name = "person #$it " }
                logger.trace { "-> Client Sent $it" }
                if(it % 10 == 0) {
                    yield()
                }
            }
        }

        val responseJob = launch(Dispatchers.IO) {
            var responseCount = 0
            responseChannel.consumeEachIndexed { (index, response) ->
                responseCount = index + 1
                logger.debug { "Bidirectional streaming response: ${response.message}" }
                yield()
            }
            responseCount shouldEqualTo requestCount
        }

        requestJob.join()
        responseJob.join()
    }
}