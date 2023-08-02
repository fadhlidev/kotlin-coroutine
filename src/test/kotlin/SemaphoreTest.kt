import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class SemaphoreTest {

    @Test
    fun `locking with semaphore test`() {
        var counter = 0
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        // Create Semaphore, with 2 permits allowing 2 coroutines access
        val semaphore = Semaphore(permits = 2)

        repeat(100) {
            scope.launch {
                repeat(100) {

                    // Lock! this block of code can be accessed by 2 coroutines
                    semaphore.withPermit {
                        counter++
                    }
                }
            }
        }

        runBlocking {
            delay(10_000)
        }

        // The final result might be not as expected
        println("Final counter: $counter")
    }

}