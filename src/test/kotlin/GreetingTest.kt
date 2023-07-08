import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class GreetingTest {

    private suspend fun greet(name: String) {
        println("(approaching $name)")
        delay(3_000)
        println("Hello, $name!")
    }

    @Test
    fun `greet test sequential`() {
        runBlocking {
            greet("Dio")
            greet("Jonathan")
            delay(5_000)
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `greet test asynchronous`() {
        // Launch new coroutine using global scope
        GlobalScope.launch {
            greet("Dio")
        }

        // Launch new coroutine using global scope
        GlobalScope.launch {
            greet("Jonathan")
        }

        // Wait for all coroutines to finish
        runBlocking {
            delay(5_000)
        }
    }

}