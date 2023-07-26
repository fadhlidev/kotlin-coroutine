import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

class CoroutineContextTest {

    @Test
    @OptIn(DelicateCoroutinesApi::class, ExperimentalStdlibApi::class)
    fun `coroutine context test`() {
        runBlocking {
            val job = GlobalScope.launch {
                // `CoroutineContext` contains `CoroutineContext.Element`s (kind of list of elements)
                val ctx = coroutineContext
                println(ctx)

                // Currently, in this context:
                // The first element is the coroutine id, but it is an internal API, so it is not accessible
                // println(ctx[CoroutineId])

                // The second element is the coroutine job
                println(ctx[Job])

                // and, the third element is the coroutine dispatcher
                println(ctx[CoroutineDispatcher])
            }

            job.join()
        }
    }

}