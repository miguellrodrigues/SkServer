import com.miguel.entities.SHome
import com.miguel.entities.SLocation
import org.junit.jupiter.api.Test
import java.util.*

internal class TestsTools {

    @Test
    internal fun printHash() {
        val loc = SLocation("world", 3.1, .2, .3)
        val home = SHome(UUID.randomUUID(), loc)

        println(home.hashCode())
    }
}