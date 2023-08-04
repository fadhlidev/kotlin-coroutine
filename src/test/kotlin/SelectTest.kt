import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select
import org.junit.jupiter.api.Test

class SelectTest {

    @Test
    fun `select deferred function`() {
        val scope = CoroutineScope(Dispatchers.IO)
        val deferred1 = scope.async {
            delay(4_000)
            1000
        }
        val deferred2 = scope.async {
            delay(2_000)
            2000
        }

        runBlocking {
            // Get the fastest value returned from async functions (deferred)
            val value = select {
                deferred1.onAwait { it }
                deferred2.onAwait { it }
            }

            println("Value: $value")
        }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `select channel function`() {
        val scope = CoroutineScope(Dispatchers.IO)
        val channel1 = scope.produce {
            delay(4_000)
            send(1000)
        }
        val channel2 = scope.produce {
            delay(2_000)
            send(2000)
        }

        runBlocking {
            // Get the fastest value returned from channels
            val value = select {
                channel1.onReceive { it }
                channel2.onReceive { it }
            }

            println("Value: $value")
        }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `select deferred and channel function`() {
        val scope = CoroutineScope(Dispatchers.IO)
        val channel = scope.produce {
            delay(4_000)
            send(1000)
        }
        val deferred = scope.async {
            delay(2_000)
            2000
        }

        runBlocking {
            // Get the fastest value returned from channels and async functions
            val value = select {
                channel.onReceive { it }
                deferred.onAwait { it }
            }

            println("Value: $value")
        }
    }

}