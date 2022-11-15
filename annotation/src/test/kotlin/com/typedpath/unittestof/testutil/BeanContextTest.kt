package com.typedpath.unittestof.testutil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test

class BeanContextTest {

    class TestContext(verifier: IVerifier) : BeanContext<String>(verifier) {
        override val target = String()
    }

    @Test
    fun testExactOrdered() {
        testMatch(BeanContext.Companion.OrderingOption.OrderedExact, IVerifier::matchExactOrdered.name )
    }
    @Test
    fun testExactUnordered() {
        testMatch(BeanContext.Companion.OrderingOption.UnorderedExact, IVerifier::matchExactUnordered.name )
    }
    @Test
    fun testOrderedMatch() {
        testMatch(BeanContext.Companion.OrderingOption.Ordered, IVerifier::matchOrdered.name )
    }
    @Test
    fun testUnorderedRawMatch() {
        testMatch(BeanContext.Companion.OrderingOption.Unordered, IVerifier::matchUnordered.name )
    }

    // TODO other calls
    private fun testMatch(orderingType: BeanContext.Companion.OrderingOption, verifyMethod: String) {
        val expectedVerifierResult = listOf("a", "b")
        val verifierStub = VerifierStub(expectedVerifierResult)
        val context = TestContext(verifier = verifierStub)
        val testCalls = listOf(
            CallCentre.Call("proxy1actual1", "call1", arrayOf("hello")),
            CallCentre.Call("proxy1actual2", "call2", arrayOf("hello2"))
        )
        testCalls.forEach{context.callLog.addCall(it.proxyId, it.methodId, it.args)}
        val matchCalls = listOf(
            CallCentre.Call("proxy1match1", "call1", arrayOf("hello")),
            CallCentre.Call("proxy1match2", "call2", arrayOf("hello2"))
        )

        lateinit var actualVerifierResult:List<String>
        BeanContext.verify(context, ordering = orderingType,
            failAction = {errors->actualVerifierResult = errors}
            ) {
            matchCalls.forEach{context.callLog.addCall(it.proxyId, it.methodId, it.args)}
        }
        assertEquals(expectedVerifierResult, actualVerifierResult)
        assertIterableEquals(testCalls,
              verifierStub.callName2Params[verifyMethod]?.first
            )
        // check match calls havent been retained
        BeanContext.verify(context, ordering = BeanContext.Companion.OrderingOption.OrderedExact,
            failAction = {errors->actualVerifierResult = errors}
        ) { }
        val testCallsSupplied = verifierStub.callName2Params[IVerifier::matchExactOrdered.name]?.first
        assertEquals(testCalls.size, testCallsSupplied?.size)

    }

}