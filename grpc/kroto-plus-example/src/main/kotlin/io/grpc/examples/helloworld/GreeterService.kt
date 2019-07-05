package io.grpc.examples.helloworld

import io.grpc.Status
import io.grpc.examples.helloword.GreeterCoroutineGrpc
import io.grpc.examples.helloword.HelloReply
import io.grpc.examples.helloword.HelloRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.toList
import mu.KLogging
import kotlin.coroutines.CoroutineContext

/**
 * GreeterService
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */
class GreeterService: GreeterCoroutineGrpc.GreeterImplBase() {

    companion object: KLogging()

    val myThreadLocal = ThreadLocal.withInitial { "value" }.asContextElement()
    private val validNameRegex = Regex("[^0-9]*")

    override val initialContext: CoroutineContext
        get() = Dispatchers.IO + myThreadLocal

    override suspend fun sayHello(request: HelloRequest): HelloReply {
        if(request.name.matches(validNameRegex)) {
            return HelloReply.newBuilder()
                .setMessage("Hello there, ${request.name}!")
                .build()
        } else {
            throw Status.INVALID_ARGUMENT.asRuntimeException()
        }
    }

    override suspend fun sayHelloClientStreaming(requestChannel: ReceiveChannel<HelloRequest>): HelloReply {
        val requestString = requestChannel.toList().joinToString()
        return HelloReply.newBuilder()
            .setMessage(requestString)
            .build()
    }

    override suspend fun sayHelloServerStreaming(request: HelloRequest, responseChannel: SendChannel<HelloReply>) {
        request.name.forEach { char ->
            responseChannel.send { message = "Hello $char!" }
        }
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    override suspend fun sayHelloStreaming(requestChannel: ReceiveChannel<HelloRequest>,
                                           responseChannel: SendChannel<HelloReply>) {
        requestChannel.consumeEach { request ->
            responseChannel.send { message = "Hello there, ${request.name}!" }
        }
    }
}