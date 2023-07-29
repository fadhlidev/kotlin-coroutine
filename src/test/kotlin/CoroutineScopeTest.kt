import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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

    @Test
    fun `coroutine scope function test`() {
        suspend fun <T> getValue(value: T): T {
            delay(2_000)
            return value
        }

        // Bundle simple coroutine processes without creating scope using CoroutineScope
        suspend fun sum(n1: Int, n2: Int): Int = coroutineScope {
            val x = async { getValue(n1) }
            val y = async { getValue(n2) }
            x.await() + y.await()
        }

        runBlocking {
            val result = async { sum(100, 200) }
            assertEquals(300, result.await())
        }
    }

}