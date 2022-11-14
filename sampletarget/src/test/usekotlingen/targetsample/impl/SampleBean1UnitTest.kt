package targetsample.impl

import com.typedpath.unittestof.annotation.UnitTest
import targetsample.Bean1
import kotlin.test.Test
import kotlin.test.assertEquals
import com.typedpath.unittestof.annotation.UnitTestOf
import org.junit.Before
import targetsample.Bean1TestContext
import targetsample.IService1ProxyContext
import com.typedpath.unittestof.testutil.BeanContext.Companion.verify
import com.typedpath.unittestof.testutil.BeanContext.Companion.verifyOrdered
import com.typedpath.unittestof.testutil.BeanContext.Companion.verifyOrderedExact
import com.typedpath.unittestof.testutil.BeanContext.Companion.verifyUnordered
import com.typedpath.unittestof.testutil.BeanContext.Companion.verifyUnorderedExact
import com.typedpath.unittestof.testutil.CallCentre.Match
import com.typedpath.unittestof.testutil.CallCentre.Match.Companion.any
import kotlin.test.assertTrue

@UnitTest
class SampleBean1UnitTest : UnitTestOf<Bean1> {

    lateinit var context : Bean1TestContext

    @Before
    fun setup() {
        context = Bean1TestContext()
    }

    @Test
    // typically when there is a clear relationship between output and interaction with dependencies
    fun testProxyExactArgMatch() {
        context.service1Recorder.whenSayHello(Match("hi"),"hello")
        val greeting = context.target.greet(listOf("hi"))
        assertEquals("hello", greeting)
    }

    @Test
    //verify params sent to services with captured
    fun testLooseArgMatchingVerifyCallsWithCaptured() {
        context.service1Recorder.whenSayHello(any(), any(), "tweeet")
        val greeting = context.target.officialGreet(listOf(Pair("Mr", "Pigeon")))
        assertEquals(greeting, "tweeet")
        assertEquals(
            IService1ProxyContext.sayHello_String_StringParams("Mr", "Pigeon"),
            context.service1Recorder.captureSayHello(any(), any()).get(0)
        )
    }

    fun setupVerifyTest() {
        context.service1Recorder.whenSayHello(any(), "cheep cheep")
        context.service1Recorder.whenSayHello(any(), any(), "tweeet")
        val greeting = context.target.greet(listOf("Gruffalo"))
        val greeting2 = context.target.officialGreet(listOf(Pair("Mrs", "Pigeon")))
        assertEquals(greeting, "cheep cheep")
        assertEquals(greeting2, "tweeet")
    }

    @Test
    fun testProxyNoArgMatchingVerifyCallsUnordered() {
        setupVerifyTest()
        var verificationErrorMessage: String? =null
        try {
            // this follows Mockk - throws an assertion if calls dont match
            verifyUnordered(context) {
                service1.sayHello("What", "Ever")
            }
            throw Exception("should fail but passed")
        } catch (ae: AssertionError) {
            // passed - expected error
            verificationErrorMessage = ae.message
        }
        assertTrue( verificationErrorMessage!=null,
            "verificationMessage: $verificationErrorMessage",)
        //should be order independant
        verifyUnordered (context) {
            service1.sayHello("Mrs", "Pigeon")
            service1.sayHello("Gruffalo")
        }
        //TODO equivalent of mockk confirmVerified
    }

    @Test
    fun testProxyNoArgMatchingVerifyCallsOrdered() {
        setupVerifyTest()
        var verificationErrorMessage: String? =null
        try {
            // this follows Mockk - throws an assertion if calls dont match
            verifyOrdered(context) {
                service1.sayHello("Mrs", "Pigeon")
                service1.sayHello("Gruffalo")
            }
            throw Exception("should fail but passed")
        } catch (ae: AssertionError) {
            // passed - expected error
            verificationErrorMessage = ae.message
        }
        assertTrue( verificationErrorMessage!=null,
            "verificationMessage: $verificationErrorMessage",)
        //should be order independant
        verifyOrdered (context) {
            service1.sayHello("Gruffalo")
            service1.sayHello("Mrs", "Pigeon")
        }
        //TODO equivalent of mockk confirmVerified
    }

    @Test
    fun testProxyNoArgMatchingVerifyCallsUnorderedExact() {
        setupVerifyTest()
        val greeting3 = context.target.officialGreet(listOf(Pair("Mr", "Parrot")))
        assertEquals(greeting3, "tweeet")
        var verificationErrorMessage: String? =null
        try {
            // this follows Mockk - throws an assertion if calls dont match
            verifyUnorderedExact(context) {
                service1.sayHello("Mrs", "Pigeon")
                service1.sayHello("Gruffalo")
            }
            throw Exception("should fail but passed")
        } catch (ae: AssertionError) {
            // passed - expected error
            verificationErrorMessage = ae.message
        }
        assertTrue( verificationErrorMessage!=null,
            "verificationMessage: $verificationErrorMessage",)
        try {
            // this follows Mockk - throws an assertion if calls dont match
            verifyUnorderedExact(context) {
                service1.sayHello("Gruffalo")
                service1.sayHello("Mr", "Parrot")
                service1.sayHello("Mr", "Parrot")
                service1.sayHello("Mrs", "Pigeon")
            }
            throw Exception("should fail but passed")
        } catch (ae: AssertionError) {
            // passed - expected error
            verificationErrorMessage = ae.message
        }
        assertTrue( verificationErrorMessage!=null,
            "verificationMessage: $verificationErrorMessage",)

        //should be order independant
        verifyUnorderedExact (context) {
            service1.sayHello("Gruffalo")
            service1.sayHello("Mr", "Parrot")
            service1.sayHello("Mrs", "Pigeon")
        }
        //TODO equivalent of mockk confirmVerified
    }

    @Test
    fun testProxyNoArgMatchingVerifyCallsOrderedExact() {
        setupVerifyTest()
        val greeting3 = context.target.officialGreet(listOf(Pair("Mr", "Parrot")))
        assertEquals(greeting3, "tweeet")
        var verificationErrorMessage: String? =null
        try {
            // this follows Mockk - throws an assertion if calls dont match
            verifyOrderedExact(context) {
                service1.sayHello("Mrs", "Pigeon")
                service1.sayHello("Gruffalo")
            }
            throw Exception("should fail but passed")
        } catch (ae: AssertionError) {
            // passed - expected error
            verificationErrorMessage = ae.message
        }
        assertTrue( verificationErrorMessage!=null,
            "verificationMessage: $verificationErrorMessage",)
        try {
            // this follows Mockk - throws an assertion if calls dont match
            verifyOrderedExact(context) {
                service1.sayHello("Gruffalo")
                service1.sayHello("Mr", "Parrot")
                service1.sayHello("Mr", "Parrot")
                service1.sayHello("Mrs", "Pigeon")
            }
            throw Exception("should fail but passed")
        } catch (ae: AssertionError) {
            // passed - expected error
            verificationErrorMessage = ae.message
        }
        assertTrue( verificationErrorMessage!=null,
            "verificationMessage: $verificationErrorMessage",)

        //should be order dependant
        try {
            // this follows Mockk - throws an assertion if calls dont match
            verifyOrderedExact(context) {
                service1.sayHello("Gruffalo")
                service1.sayHello("Mr", "Parrot")
                service1.sayHello("Mrs", "Pigeon")
            }
            throw Exception("should fail but passed")
        } catch (ae: AssertionError) {
            // passed - expected error
            verificationErrorMessage = ae.message
        }
        assertTrue( verificationErrorMessage!=null,
            "verificationMessage: $verificationErrorMessage",)


        verifyOrderedExact(context) {
            service1.sayHello("Gruffalo")
            service1.sayHello("Mrs", "Pigeon")
            service1.sayHello("Mr", "Parrot")
        }

    }

    @Test
    //TODO - should this be in extra
    fun testLooseArgMatchingVerifyFailCustomAssert() {
        context.service1Recorder.whenSayHello(any(), any(), "tweeet")
        context.service1Recorder.whenSayHello(any(), "cheep cheep")
        context.target.greet(listOf("goodbye"))
        context.target.officialGreet(listOf(Pair("Mrs", "Pigeon")))

        var errors = verify(context, { }) {
            service1.sayHello("hello")
            service1.sayHello("Mr", "Pigeon")
            service1.sayHello("Miss", "Pigeon")
        }
        println("${errors.joinToString (System.lineSeparator())}")
        assertEquals(5, errors.size)
    }

    @Test
    fun testLooseArgMatchingVerifyPassCustomAssert() {
        context.service1Recorder.whenSayHello(any(), "cheep cheep")
        context.service1Recorder.whenSayHello(any(), any(), "tweeet")

        assertEquals("cheep cheep",     context.target.greet(listOf("goodbye")))
        assertEquals("tweeet" ,context.target.officialGreet(listOf(Pair("Mrs", "Pigeon"))))

        var errors = verify(context, { }) {
            service1.sayHello("goodbye")
            service1.sayHello("Mrs", "Pigeon")
        }
        println("${errors.joinToString (System.lineSeparator())}")
        assertEquals(0, errors.size)
    }

}