import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

}