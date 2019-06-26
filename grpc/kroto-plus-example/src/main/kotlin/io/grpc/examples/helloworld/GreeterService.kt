package io.grpc.examples.helloworld

import io.grpc.Status
import io.grpc.examples.helloword.GreeterCoroutineGrpc
import io.grpc.examples.helloword.HelloReply
import io.grpc.examples.helloword.HelloRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asContextElement
import kotlin.coroutines.CoroutineContext

/**
 * GreeterService
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */
class GreeterService: GreeterCoroutineGrpc.GreeterImplBase() {

    val myThreadLocal = ThreadLocal.withInitial { "value" }.asContextElement()
    private val validNameRegex = Regex("[^0-9]*")

    override val initialContext: CoroutineContext
        get() = Dispatchers.Default + myThreadLocal

    override suspend fun sayHello(request: HelloRequest): HelloReply {
        if(request.name.matches(validNameRegex)) {
            return HelloReply.newBuilder()
                .setMessage("Hello there, ${request.name}!")
                .build()
        } else {
            throw Status.INVALID_ARGUMENT.asRuntimeException()
        }
    }
}