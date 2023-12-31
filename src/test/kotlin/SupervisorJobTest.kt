import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class SupervisorJobTest {

    @Test
    fun `coroutine job test`() {
        // Create a new scope, and `Job` into it (just to be declarative here)
        val scope = CoroutineScope(Dispatchers.IO + Job())

        val job1 = scope.launch {
            println("Start job #1")
            delay(3_000)
            println("Finish job #1")
        }

        val job2 = scope.launch {
            println("Start job #2")
            delay(1_000)

            // This error will be propagated to the top scope and then the scope will cancel all its children
            throw RuntimeException()
        }

        runBlocking {
            joinAll(job1, job2)
        }
    }

    @Test
    fun `coroutine supervisor job test`() {
        // Create a new scope, and `SupervisorJob` into it
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        val job1 = scope.launch {
            println("Start job #1")
            delay(3_000)
            println("Finish job #1")
        }

        val job2 = scope.launch {
            println("Start job #2")
            delay(1_000)

            // This error will NOT be propagated to the top scope, and all scope's children will not be canceled
            throw RuntimeException()
        }

        runBlocking {
            joinAll(job1, job2)
        }
    }

    @Test
    fun `coroutine supervisor scope test`() {
        val scope = CoroutineScope(Dispatchers.IO + Job())

        val job = scope.launch {
            println("[Parent Scope] Start Job")

            // Start a coroutine scope with SupervisorJob using `supervisorScope`
            // Every error thrown from this scope will not be propagated to its parent
            supervisorScope {
                launch {
                    println("[Supervisor Scope] Start job #1")
                    delay(3_000)
                    println("[Supervisor Scope] Finish job #1")
                }

                launch {
                    println("[Supervisor Scope] Start job #2")
                    delay(1_000)
                    throw RuntimeException()
                }
            }

            println("[Parent Scope] Finish Job")
        }

        runBlocking {
            job.join()
        }
    }

}