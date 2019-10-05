package io.github.debop.kotlin.workshop.reactive

import kotlinx.coroutines.flow.flow
import mu.KLogging
import org.junit.jupiter.api.Test

class FlowAsPublisherTest {

    companion object: KLogging()

    @Test
    fun `flow raise error - catch onCancellation`() {
        flow<Int> {
            emit(2)

        }
    }
}