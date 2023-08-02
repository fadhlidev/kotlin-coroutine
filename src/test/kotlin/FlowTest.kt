import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Test

class FlowTest {

    @Test
    fun `asynchronous flow test`() {
        // while `async` only returns a value,
        // `flow` can return sequence values
        val flow: Flow<Int> = flow {
            println("Start flow")

            repeat(10) {
                delay(1_000)

                // emit (send) value to the flow
                println("Emit $it")
                emit(it)
            }
        }

        runBlocking {
            // collect emitted values from the flow
            flow.collect {
                println("Collect $it")
                println(it)
            }
        }
    }

    @Test
    fun `flow operators test`() {
        val flow: Flow<Int> = flow {
            println("Start flow")
            repeat(100) {
                emit(it)
            }
        }

        runBlocking {
            // before calling `collect` we can use operators like in collection
            flow.filter { it % 2 == 0 }.map { "Number $it" }.collect { println(it) }
        }
    }

    @Test
    fun `flow exception test`() {
        val flow: Flow<Int> = flow {
            println("Start flow")
            repeat(100) {
                emit(it)
            }
        }

        runBlocking {
            flow.map { check(it < 10); it } // check or throw error
                .onEach { println(it) }
                .catch { println("Error: ${it.message}") }  // catch error
                .onCompletion { println("Flow finished") }  // like `finally` block
                .collect()
        }
    }

    @Test
    fun `cancel flow test`() {
        runBlocking {
            coroutineScope {
                val job = launch {
                    val flow: Flow<Int> = flow {
                        repeat(100) {
                            emit(it)
                        }
                    }

                    flow.onEach {
                        if (it > 10) cancel() // cancel job! then it will cancel the flow
                        else println(it)
                    }
                        .collect()
                }

                job.join()
            }
        }
    }

}