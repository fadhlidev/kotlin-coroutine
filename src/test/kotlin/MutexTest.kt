import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class MutexTest {

    @Test
    fun `race condition test`() {
        var counter = 0
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        repeat(100) {
            scope.launch {
                repeat(100) {
                    counter++
                }
            }
        }

        runBlocking {
            delay(10_000)
        }

        // We expect final counter will be 10000, but it doesn't...
        println("Final counter: $counter")
    }

    @Test
    fun `locking with mutex test`() {
        var counter = 0
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)

        // Create mutex (Mutual Exclusion)
        val mutex = Mutex()

        repeat(100) {
            scope.launch {
                repeat(100) {

                    // Lock! this block of code can only be accessed by one coroutine
                    mutex.withLock {
                        counter++
                    }
                }
            }
        }

        runBlocking {
            delay(10_000)
        }

        println("Final counter: $counter")
    }

}