package targetsample.impl;

import com.typedpath.unittestof.annotation.UnitTest;
import com.typedpath.unittestof.annotation.UnitTestOf;
import org.junit.Test
import targetsample.Bean2
import targetsample.Bean2TestContext
import  com.typedpath.unittestof.testutil.BeanContext.Companion.verify
import  com.typedpath.unittestof.testutil.CallCentre.Match.Companion.any
import kotlin.test.assertEquals

@UnitTest
class SampleBean2UnitTest : UnitTestOf<Bean2>{

    @Test
    fun test() {
        val context = Bean2TestContext()
        context.service1Recorder.whenSayHello(any(), "hello back")
        context.service2Recorder.whenLog(any(), any(), "logged!")
        val result = context.target.dostuff("hello")
        val hellosSent = context.service1Recorder.captureSayHello(any())
        assertEquals("hello", hellosSent[0])
        val logsSent = context.service2Recorder.captureLog(any(), any())
        println(logsSent)
        assertEquals(logsSent[0].severity, "critical")
        assertEquals(logsSent[0].text, "doing stuff")

        verify (context) {
            service1.sayHello("hello")
            service2.log("critical", "doing stuff")
        }
    }


}
