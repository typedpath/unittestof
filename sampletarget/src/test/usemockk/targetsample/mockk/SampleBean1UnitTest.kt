package targetsample.mockk

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.Before
import targetsample.Bean1
import targetsample.IService1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// TODO add spy example
class SampleBean1UnitTest {

    @MockK
    lateinit var service1: IService1

    @InjectMockKs
    lateinit var target: Bean1

    @Before
    fun setUp() = MockKAnnotations.init(this)

    val nameSlot = slot<String>()
    val lastNameSlot = slot<String>()

    @Test
    // typically when there is a clear relationship between output and interaction with dependencies
    fun testProxyExactArgMatch() {
        every { service1.sayHello("hi") } returns "hello"
        val greeting = target.greet(listOf("hi"))
        assertEquals(greeting, "hello")
    }

    @Test
    // capturing uses slots!
    fun testLooseArgMatchingVerifyCallsWithCaptured() {
        every { service1.sayHello(capture(nameSlot), capture(lastNameSlot)) } returns "tweeet"
        val greeting = target.officialGreet(listOf(Pair("Mr", "Pigeon")))
        assertEquals(greeting, "tweeet")
        assertEquals(nameSlot.captured, "Mr")
        assertEquals(lastNameSlot.captured, "Pigeon")
    }

    fun setupVerifyTest() {
        every { service1.sayHello(any()) } returns "cheep cheep"
        every { service1.sayHello(any(), any()) } returns "tweeet"
        val greeting = target.greet(listOf("Gruffalo"))
        val greeting2 = target.officialGreet(listOf(Pair("Mrs", "Pigeon")))
        assertEquals(greeting, "cheep cheep")
        assertEquals(greeting2, "tweeet")
    }

    @Test
    //verify params sent to services
    fun testProxyNoArgMatchingVerifyCallsUnordered() {
        setupVerifyTest()
        var verificationErrorMessage: String? = null
        try {
            verify(ordering = Ordering.UNORDERED) {
                service1.sayHello("What", "ever")
            }
        } catch (ae: AssertionError) {
            verificationErrorMessage = ae.message
        }
        assertTrue(
            verificationErrorMessage != null && verificationErrorMessage.contains("Verification failed"),
            "verificationMessage: $verificationErrorMessage",
        )
        //should be order independant
        verify(ordering = Ordering.UNORDERED) {
            service1.sayHello("Mrs", "Pigeon")
            service1.sayHello("Gruffalo")
        }
        confirmVerified(service1)
    }

    @Test
    //verify params sent to services
    fun testProxyNoArgMatchingVerifyCallsOrdered() {
        setupVerifyTest()
        var verificationErrorMessage: String? = null
        try {
            verify(ordering = Ordering.ORDERED) {
                service1.sayHello("Mrs", "Pigeon")
                service1.sayHello("Gruffalo")
            }
        } catch (ae: AssertionError) {
            verificationErrorMessage = ae.message
        }
        assertTrue(
            verificationErrorMessage != null && verificationErrorMessage.contains("Verification failed"),
            "verificationMessage: $verificationErrorMessage",
        )
        //should be order independant
        verify(ordering = Ordering.ORDERED) {
            service1.sayHello("Gruffalo")
            service1.sayHello("Mrs", "Pigeon")
        }
        confirmVerified(service1)
    }

    //
    @Test
    //verify params sent to services
    fun testProxyNoArgMatchingVerifyCallsUnorderedExact() {
        setupVerifyTest()
        val greeting3 = target.officialGreet(listOf(Pair("Mr", "Parrot")))
        assertEquals(greeting3, "tweeet")

        var verificationErrorMessage: String? = null
        try {
            verifyAll {
                service1.sayHello("Mrs", "Pigeon")
                service1.sayHello("Gruffalo")
            }
        } catch (ae: AssertionError) {
            verificationErrorMessage = ae.message
        }
        assertTrue(
            verificationErrorMessage != null && verificationErrorMessage.contains("Verification failed"),
            "verificationMessage: $verificationErrorMessage",
        )

        try {
            verifyAll {
                service1.sayHello("Gruffalo")
                service1.sayHello("Mr", "Parrot")
                service1.sayHello("Mr", "Parrot")
                service1.sayHello("Mrs", "Pigeon")
            }
        } catch (ae: AssertionError) {
            verificationErrorMessage = ae.message
        }
        assertTrue(
            verificationErrorMessage != null && verificationErrorMessage.contains("Verification failed"),
            "verificationMessage: $verificationErrorMessage",
        )

        //should be order independant
        verifyAll {
            service1.sayHello("Gruffalo")
            service1.sayHello("Mr", "Parrot")
            service1.sayHello("Mrs", "Pigeon")
        }
    }

    @Test
    //verify params sent to services
    fun testProxyNoArgMatchingVerifyCallsOrderedExact() {
        setupVerifyTest()
        val greeting3 = target.officialGreet(listOf(Pair("Mr", "Parrot")))
        assertEquals(greeting3, "tweeet")

        var verificationErrorMessage: String? = null
        try {
            verifySequence {
                service1.sayHello("Mrs", "Pigeon")
                service1.sayHello("Gruffalo")
            }
        } catch (ae: AssertionError) {
            verificationErrorMessage = ae.message
        }
        assertTrue(
            verificationErrorMessage != null && verificationErrorMessage.contains("Verification failed"),
            "verificationMessage: $verificationErrorMessage",
        )

        try {
            verifySequence {
                service1.sayHello("Gruffalo")
                service1.sayHello("Mr", "Parrot")
                service1.sayHello("Mr", "Parrot")
                service1.sayHello("Mrs", "Pigeon")
            }
        } catch (ae: AssertionError) {
            verificationErrorMessage = ae.message
        }
        assertTrue(
            verificationErrorMessage != null && verificationErrorMessage.contains("Verification failed"),
            "verificationMessage: $verificationErrorMessage",
        )

        //should be not be order independant
        try {
            verifySequence {
                service1.sayHello("Gruffalo")
                service1.sayHello("Mr", "Parrot")
                service1.sayHello("Mrs", "Pigeon")
            }
        } catch (ae: AssertionError) {
            verificationErrorMessage = ae.message
        }

        assertTrue(
            verificationErrorMessage != null,
            "verificationMessage: $verificationErrorMessage",
        )

        verifySequence {
            service1.sayHello("Gruffalo")
            service1.sayHello("Mrs", "Pigeon")
            service1.sayHello("Mr", "Parrot")
        }

    }

}