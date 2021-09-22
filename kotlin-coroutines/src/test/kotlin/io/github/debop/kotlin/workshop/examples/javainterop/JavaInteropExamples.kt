package io.github.debop.kotlin.workshop.examples.javainterop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

/**
 * JavaInteropExamples
 * @author debop (Sunghyouk Bae)
 */

data class Image(val content: ByteArray)

fun loadImageAsync(name: String): CompletableFuture<Image> {
    TODO("구현 중")
}

fun combineImages(image1: Image, image2: Image): Image {
    TODO("구현 중")
}

// Java CompletableFuture
fun loadAndCombineAsync(name1: String, name2: String): CompletableFuture<Image> {

    val future1 = loadImageAsync(name1)
    val future2 = loadImageAsync(name2)

    return future1.thenCompose { image1 ->
        future2.thenCompose { image2 ->
            CompletableFuture.supplyAsync {
                combineImages(image1, image2)
            }
        }
    }
}

// Kotlin Coroutines
fun loadAndCombine(name1: String, name2: String): CompletableFuture<Image> {
    val scope = CoroutineScope(Dispatchers.Default)

    return scope.future {
        val future1 = loadImageAsync(name1)
        val future2 = loadImageAsync(name2)

        combineImages(future1.await(), future2.await())
    }
}

