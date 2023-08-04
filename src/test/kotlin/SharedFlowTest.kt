import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.buffer
import org.junit.jupiter.api.Test

class SharedFlowTest {

    @Test
    fun `shared flow test`() {
        // If flow can only have one collector
        // Shared flow can have multiple collectors
        val sharedFlow = MutableSharedFlow<Int>(replay = 10)

        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            repeat(10) {
                delay(1_000)
                sharedFlow.emit(it)
            }
        }

        scope.launch {
            // use `asSharedFlow` function to create SharedFlow
            sharedFlow.asSharedFlow()
                .buffer(10) // can add buffer
                .collect {
                    delay(1_000)
                    println("Shared flow 1 receive $it")
                }
        }

        scope.launch {
            sharedFlow.asSharedFlow()
                .buffer(10)
                .collect {
                    delay(2_000)
                    println("Shared flow 2 receive $it")
                }
        }

        runBlocking {
            delay(12_000)
            scope.cancel()
        }
    }

}