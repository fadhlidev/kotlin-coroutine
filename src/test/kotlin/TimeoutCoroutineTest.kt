import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class TimeoutCoroutineTest {

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `manual timeout test`() {
        // Launch a new coroutine job
        val job = GlobalScope.launch {
            println("Start Job")
            delay(5_000)
            println("Job Finished")
        }

        // Launch a new coroutine to cancel previous coroutine
        GlobalScope.launch {
            delay(3_000)
            job.cancel()
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `with timeout test`() {
        runBlocking {
            val job = GlobalScope.launch {
                println("Start Job")

                // Run a job with specified timeout
                // It will throw TimeoutCancellationException
                withTimeout(3_000) {
                    delay(5_000)
                }

                println("Job Finished")
            }

            job.join()
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `with timeout or null test`() {
        runBlocking {
            val job = GlobalScope.launch {
                println("Start Job")

                // Run a job with specified timeout
                // It will NOT throw TimeoutCancellationException, instead it will return null
                withTimeoutOrNull(3_000) {
                    delay(5_000)
                }

                println("Job Finished")
            }

            job.join()
        }
    }

}