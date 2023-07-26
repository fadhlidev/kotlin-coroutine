import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class CoroutineDispatcherTest {

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `coroutine dispatcher test`() {
        // `CoroutineDispatcher` is responsible to decide which thread is going to be used

        println("Thread running for this unit test: ${Thread.currentThread().name}")

        runBlocking {
            println("Thread running for this runBlocking: ${Thread.currentThread().name}")

            // `Dispatchers.Default` -> total threads will be which one is greater between 2 or max CPU
            val job1 = GlobalScope.launch(Dispatchers.Default) {
                println("Thread running for Job 1: ${Thread.currentThread().name}")
            }

            // `Dispatcher.IO` -> It will create or remove threads on demand, it is also the default parameter
            // Basically it will share with Dispatcher.Default until it needs more threads
            val job2 = GlobalScope.launch(Dispatchers.IO) {
                println("Thread running for Job 2: ${Thread.currentThread().name}")
            }

            // There is another dispatcher called `Dispatcher.Main` which usually used for UI

            joinAll(job1, job2)
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `confined and unconfined dispatchers test`() {
        runBlocking {
            // `Dispatchers.Unconfined` -> at some point we can change the thread used in this coroutine
            val job1 = GlobalScope.launch(Dispatchers.Unconfined) {
                println("Thread running for Job 1: ${Thread.currentThread().name}")
                delay(1_000)

                // The current used thread might be different from the first one
                println("Thread running for Job 1: ${Thread.currentThread().name}")
            }

            // `Dispatcher.Confined` -> it is the default parameter, so we don't need to pass it manually
            // By using this dispatcher, we cannot change the thread being used in this coroutine
            val job2 = GlobalScope.launch {
                println("Thread running for Job 2: ${Thread.currentThread().name}")
                delay(1_000)

                // The current used thread will always be the same as the first one
                println("Thread running for Job 2: ${Thread.currentThread().name}")
            }

            // There is another dispatcher called `Dispatcher.Main` which usually used for UI

            joinAll(job1, job2)
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `custom dispatchers test`() {
        // It is recommended to create custom dispatcher for application layers as needed
        // Use Executors service with `asCoroutineDispatcher` extension function to create a custom dispatcher
        val controllerDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val repositoryDispatcher = Executors.newFixedThreadPool(20).asCoroutineDispatcher()

        runBlocking {
            val controllerJob = GlobalScope.launch(controllerDispatcher) {
                println("Thread running for controllerJob: ${Thread.currentThread().name}")
            }

            val repositoryJob = GlobalScope.launch(repositoryDispatcher) {
                println("Thread running for repositoryJob: ${Thread.currentThread().name}")
            }

            joinAll(controllerJob, repositoryJob)
        }
    }

    @Test
    @OptIn(DelicateCoroutinesApi::class)
    fun `switching context or dispatcher test`() {
        val serviceDispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()

        runBlocking {
            val job = GlobalScope.launch(Dispatchers.IO) {
                println("[Step 1] Thread running for job: ${Thread.currentThread().name}")

                // To switch context/dispatcher, use `withContext` function
                withContext(serviceDispatcher) {
                    println("[Step 2] Thread running for job: ${Thread.currentThread().name}")
                }

                println("[Step 3] Thread running for job: ${Thread.currentThread().name}")
            }

            job.join()
        }
    }

}