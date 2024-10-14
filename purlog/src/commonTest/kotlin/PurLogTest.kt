import com.metashark.purlog.PurLog
import com.metashark.purlog.enums.PurLogEnv
import com.metashark.purlog.enums.PurLogLevel
import com.metashark.purlog.models.PurLogConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

internal class PurLogTests {

    @Test
    fun testInitializeConfig() = runTest {
        // Arrange
        val config = PurLogConfig(level = PurLogLevel.INFO, env = PurLogEnv.PROD)

        // Act
        PurLog.shared.initialize(config)

        // Assert
        assertEquals(PurLogLevel.INFO, config.level)
        assertEquals(PurLogEnv.PROD, config.env)
    }
}