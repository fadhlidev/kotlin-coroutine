import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class CoroutineJobTest {

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `greet test cancel job`() {
        // Launch new coroutine using global scope
        val job = GlobalScope.launch {
            println("Job : ${Thread.currentThread().name}")
            delay(2_000)
        }

        // Cancel the coroutine job
        job.cancel()

        runBlocking {
            delay(3_000)
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `greet test start job`() {
        // Create a new coroutine job using global scope launch
        val job = GlobalScope.launch(start = CoroutineStart.LAZY) {
            println("Job : ${Thread.currentThread().name}")
            delay(2_000)
        }

        // Start the coroutine job
        job.start()

        runBlocking {
            delay(3_000)
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `greet test wait job`() {
        val job = GlobalScope.launch(start = CoroutineStart.LAZY) {
            println("Job : ${Thread.currentThread().name}")
            delay(2_000)
        }

        job.start()

        runBlocking {
            // Wait for the job to finish
            job.join()
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `greet test wait jobs`() {
        val job1 = GlobalScope.launch {
            println("Job 1 : ${Thread.currentThread().name}")
            delay(2_000)
        }

        val job2 = GlobalScope.launch {
            println("Job 2 : ${Thread.currentThread().name}")
            delay(3_000)
        }

        job1.start()
        job2.start()

        // Wait for all jobs to finish
        runBlocking {
            joinAll(job1, job2)
        }
    }

}