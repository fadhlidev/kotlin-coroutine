import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

class AsyncFunctionTest {

    suspend fun <T> getValue(value: T): T {
        delay(1_000)
        return value
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `run async function test`() {
        runBlocking {
            val time = measureTimeMillis {
                // Run coroutine using `async` function
                // Returns: `Deferred<T>`
                val x = GlobalScope.async {
                    getValue(50)
                }

                val y = GlobalScope.async {
                    getValue(50)
                }

                // Use `await` function to wait and get the value `T` from the `Deferred<T>`
                val res = x.await() + y.await()
                assertEquals(100, res)
            }

            println("Execution time: $time")
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `await all async functions test`() {
        runBlocking {
            val time = measureTimeMillis {
                val x = GlobalScope.async {
                    getValue(50)
                }

                val y = GlobalScope.async {
                    getValue(50)
                }

                // Use `awaitAll` function to wait all async coroutines
                // This function will return `List<T>` based on the returned deferred values
                val res = awaitAll(x, y).sum()
                assertEquals(100, res)
            }

            println("Execution time: $time")
        }
    }

}