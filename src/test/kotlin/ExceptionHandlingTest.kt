import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class ExceptionHandlingTest {

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `exception launch test`() {
        runBlocking {
            // `launch` and `join` will not propagate thrown error (silent)
            val job = GlobalScope.launch {
                println("Start launch job")
                throw RuntimeException()
            }

            job.join()

            // This line bellow still be executed
            println("run blocking done")
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `exception async test`() {
        runBlocking {
            // `async` and `await` will propagate thrown error
            val job = GlobalScope.async {
                println("Start async job")
                throw RuntimeException()
            }

            // This will propagate error thrown from the async job,
            // so it is recommended to add try-catch whenever using `async` and `await`
            job.await()

            // Unreachable
            println("run blocking done")
        }
    }

    @Test
    fun `exception handler test`() {
        // Creating custom exception handler
        // It is applicable with `launch` and `join` but not with `async` and `await`
        // Still needs manually add try-catch if using `async` and `await`
        val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
            println("An error occurred. ${throwable.message}")
        }

        val scope = CoroutineScope(Dispatchers.IO + exceptionHandler)
        val job = scope.launch {
            println("Start job")
            throw RuntimeException("Something wrong")
        }

        runBlocking {
            job.join()
        }
    }

}