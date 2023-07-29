import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class CoroutineScopeTest {

    @Test
    fun `create scope test`() {
        val scope = CoroutineScope(Dispatchers.IO)

        val job1 = scope.launch {
            delay(1_000)
            println("Job #1")
        }

        val job2 = scope.launch {
            delay(1_000)
            println("Job #2")
        }

        runBlocking {
            joinAll(job1, job2)
        }
    }

    @Test
    fun `cancel scope test`() {
        val scope = CoroutineScope(Dispatchers.IO)

        val job1 = scope.launch {
            delay(2_000)
            println("Job #1")
        }

        val job2 = scope.launch {
            delay(2_000)
            println("Job #2")
        }

        runBlocking {
            delay(1_000)

            // This will cancel the scope, so all coroutines running using this scope is also cancelled
            scope.cancel()
            joinAll(job1, job2)
        }
    }

}