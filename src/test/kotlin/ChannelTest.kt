import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import org.junit.jupiter.api.Test

class ChannelTest {

    @Test
    fun `create channel test`() {
        runBlocking {
            // Create a new channel
            val channel = Channel<Int>()

            val job1 = launch {
                println("Job 1 Send: 50")
                channel.send(50) // Send value into the channel
                println("Job 1 Send: 80")
                channel.send(80)
            }

            val job2 = launch {
                println("Job 2 Receive: ${channel.receive()}") // Receive value from the channel
                println("Job 2 Receive: ${channel.receive()}")
            }

            joinAll(job1, job2)

            // Important! always close the channel
            channel.close()
        }
    }

    @Test
    fun `buffered channel test`() {
        runBlocking {
            // Create a new buffered channel (with capacity)
            val channel = Channel<Int>(capacity = 10)

            val job1 = launch {
                repeat(10) {
                    println("Job 1 Send: $it")
                    channel.send(it)
                }
            }

            val job2 = launch {
                repeat(10) {
                    delay(1_000) // to simulates that receiver is slower than sender
                    println("Job 2 Receive: ${channel.receive()}")
                }
            }

            joinAll(job1, job2)
            channel.close()
        }
    }

    @Test
    fun `buffer overflow channel test`() {
        runBlocking {
            // Create a new buffered channel with overflow action
            val channel = Channel<Int>(capacity = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)

            val job1 = launch {
                repeat(20) {
                    println("Job 1 Send: $it")
                    channel.send(it)
                }
            }

            val job2 = launch {
                repeat(10) {
                    delay(1_000)
                    println("Job 2 Receive: ${channel.receive()}")
                }
            }

            joinAll(job1, job2)
            channel.close()
        }
    }

    @Test
    fun `channel undelivered element test`() {
        runBlocking {
            // Can add a lambda function to a channel to handle undelivered element (value)
            val channel = Channel<Int>(Channel.UNLIMITED) {
                println("Undelivered value $it")
            }

            // Close the channel immediately
            channel.close()

            val job = launch {
                // Send value to closed channel
                channel.send(100)
            }

            job.join()
        }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `produce function test`() {
        val scope = CoroutineScope(Dispatchers.IO)

        // Dedicated function to create a channel and then returns its receiver
        val channel = scope.produce(capacity = 10) {
            repeat(10) {
                send(it)
            }
        }

        val job = scope.launch {
            repeat(10) {
                println("Receive ${channel.receive()}")
            }
        }

        runBlocking {
            job.join()
        }
    }

}