import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class SuspendFunctionTest {

    suspend fun <T> getValue(value: T): T {
        delay(1_000)
        return value
    }

    @Test
    fun `run sequential test`() {
        runBlocking {
            val time = measureTimeMillis {
                // These suspend functions will run sequentially
                getValue("Hello")
                getValue(106)
            }

            println("Execution time : $time")
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `run concurrent test`() {
        runBlocking {
            val time = measureTimeMillis {
                // These lines bellow will run the suspend functions concurrently
                // But, none of them will return the expected value, instead the returned value will be `Job` object
                val job1 = GlobalScope.launch { getValue("Hello") }
                val job2 = GlobalScope.launch { getValue(106) }

                joinAll(job1, job2)
            }

            println("Execution time : $time")
        }
    }

}