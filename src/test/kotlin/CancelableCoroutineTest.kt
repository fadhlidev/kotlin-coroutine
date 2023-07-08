import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class CancelableCoroutineTest {

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `not cancelable coroutine test`() {
        runBlocking {
            val job = GlobalScope.launch {
                println("Start job")

                // Thread.sleep cant be canceled
                Thread.sleep(2_000)

                println("Finish job")
            }

            job.cancel()
            job.join()
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `cancelable coroutine test`() {
        runBlocking {
            val job = GlobalScope.launch {
                // Manually check if coroutine is still active
                if (!isActive) throw CancellationException()
                println("Start job")

                // Built-in function to check if coroutine is still active
                ensureActive()
                Thread.sleep(2_000)

                ensureActive()
                println("Finish job")
            }

            job.cancel()
            job.join()
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `post cancel coroutine test`() {
        runBlocking {
            val job = GlobalScope.launch {
                try {
                    println("Start job")

                    // delay() is a cancelable
                    delay(2_000)

                    println("Finish job")
                } finally {
                    // Run code after coroutine cancelled
                    println("Finally!")
                }
            }

            job.cancelAndJoin()
        }
    }

}