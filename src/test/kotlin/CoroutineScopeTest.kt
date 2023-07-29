import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
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

    @Test
    fun `parent child dispatcher test`() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val parentScope = CoroutineScope(dispatcher)

        val job = parentScope.launch {
            println("Parent scope runs on thread: ${Thread.currentThread().name}")

            coroutineScope {
                launch {
                    // Child scope will use the same context as its parent
                    println("Child scope runs on thread: ${Thread.currentThread().name}")
                }
            }
        }

        runBlocking {
            job.join()
        }
    }

    @Test
    fun `parent child cancel test`() {
        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        val parentScope = CoroutineScope(dispatcher)

        val job = parentScope.launch {
            println("Parent scope runs on thread: ${Thread.currentThread().name}")

            coroutineScope {
                launch {
                    delay(2_000)

                    // This code is unreachable because its parent has been cancelled
                    println("Child scope runs on thread: ${Thread.currentThread().name}")
                }
            }
        }

        runBlocking {
            job.cancelAndJoin()
        }
    }

    @Test
    fun `parent child coroutine test`() {
        val scope = CoroutineScope(Dispatchers.IO)

        val job = scope.launch {
            // Launch another coroutines inside a parent coroutine
            launch {
                delay(2_000)
                println("Child coroutine #1 finished!")
            }

            launch {
                delay(3_000)
                println("Child coroutine #2 finished!")
            }

            delay(1_000)
            println("Parent coroutine finished!")
        }

        runBlocking {
            // The parent coroutine will wait all its child to finish
            job.join()
        }
    }

    @Test
    fun `cancelChildren test`() {
        val scope = CoroutineScope(Dispatchers.IO)

        val job = scope.launch {
            launch {
                delay(2_000)
                println("Child coroutine #1 finished!")
            }

            launch {
                delay(3_000)
                println("Child coroutine #2 finished!")
            }

            delay(1_000)
            println("Parent coroutine finished!")
        }

        runBlocking {
            job.cancelChildren()
            job.join()
        }
    }

}