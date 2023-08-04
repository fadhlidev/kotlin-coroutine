import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Test

class StateFlowTest {

    @Test
    fun `state flow test`() {
        // Similar with ShareFlow but it only receives latest data
        val sharedFlow = MutableStateFlow(0) // initial value

        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            repeat(10) {
                delay(1_000)
                sharedFlow.emit(it + 1)
            }
        }

        scope.launch {
            // use `asStateFlow` function to create StateFlow
            sharedFlow.asStateFlow()
                .collect {
                    delay(1_000)
                    println("Shared flow 1 receive $it")
                }
        }

        scope.launch {
            sharedFlow.asStateFlow()
                .collect {
                    delay(5_000)
                    println("Shared flow 2 receive $it")
                }
        }

        runBlocking {
            delay(12_000)
            scope.cancel()
        }
    }

}