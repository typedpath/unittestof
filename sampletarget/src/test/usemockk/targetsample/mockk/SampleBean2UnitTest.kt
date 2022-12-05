package targetsample.mockk;

import org.junit.Before
import org.junit.Test
import targetsample.Bean2
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import targetsample.IService1
import targetsample.IService2
import kotlin.test.assertEquals

class SampleBean2UnitTest {

    @MockK
    lateinit var service1: IService1

    @MockK
    lateinit var service2: IService2

    @InjectMockKs
    lateinit var target: Bean2

    @Before
    fun setUp() = MockKAnnotations.init(this)

    val nameSlot = slot<String>()
    val logSeveritySlot = slot<String>()
    val logTextSlot = slot<String>()

    @Test
    fun test() {
        every { service1.sayHello(capture(nameSlot))} returns "hello back"
        every { service2.log(capture(logSeveritySlot), capture(logTextSlot))}  returns "logged!"
        val result = target.dostuff("hello")
        assertEquals("hello back", result);
        val helloSent = nameSlot.captured
        assertEquals("hello", helloSent)
        assertEquals( "critical", logSeveritySlot.captured)
        assertEquals( "doing stuff", logTextSlot.captured)

        verify  {
            service1.sayHello("hello")
            service2.log("critical", "doing stuff")
        }
    }


}
