package com.typedpath.unittestof.testutil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestVerify {

    private val verifier = Verifier()

    @Test
    fun testExactUnorderedRaw() {
        val testCalls = listOf(
        CallCentre.Call("proxy1", "call2", arrayOf("hello")),
        CallCentre.Call("proxy1", "call1", arrayOf("hello")))

        val matchCalls = listOf(
        CallCentre.Call("proxy1", "call1", arrayOf("hello")),
        CallCentre.Call("proxy1", "call2", arrayOf("hello")))

        val errors = verifier.matchExactUnordered(testCalls, matchCalls)

        assertEquals(0, errors.size)

        //BeanContext.verify(context, ordering = BeanContext.Companion.OrderingOption.UnorderedExact) {
        }

    @Test
    fun testExactUnorderedRawUnmatchedExactFail() {
        val testCalls = listOf(
                    CallCentre.Call("proxy1", "call1", arrayOf("hello")),
                    CallCentre.Call("proxy3", "call1", arrayOf("hello")),
                    CallCentre.Call("proxy1", "call2", arrayOf("hello")))

        val matchCalls = listOf(
                CallCentre.Call("proxy1", "call1", arrayOf("hello")),
                CallCentre.Call("proxy1", "call2", arrayOf("hello")))

        val errors = verifier.matchExactUnordered(testCalls, matchCalls)

        assertEquals(4, errors.size)
        //TODO check errors content
        errors.forEach { println(it) }
    }

    @Test
    fun testExactOrderedRawMatch() {
        val testCalls = listOf(
            CallCentre.Call("proxy1", "call1", arrayOf("hello")),
            CallCentre.Call("proxy1", "call2", arrayOf("hello")))
        val matchCalls = listOf(
            CallCentre.Call("proxy1", "call1", arrayOf("hello")),
            CallCentre.Call("proxy1", "call2", arrayOf("hello")))
        val errors = verifier.matchExactOrdered(testCalls, matchCalls)
        assertEquals(0, errors.size)

    }


    @Test
    fun testExactOrderedRawFailOrderMatch() {
        val testCalls = listOf(
            CallCentre.Call("proxy1", "call2", arrayOf("hello")),
            CallCentre.Call("proxy1", "call1", arrayOf("hello")))
        val matchCalls = listOf(
            CallCentre.Call("proxy1", "call1", arrayOf("hello")),
            CallCentre.Call("proxy1", "call2", arrayOf("hello")))

        val errors = verifier.matchExactOrdered(testCalls, matchCalls)
        //TODO check content of errors
        assertEquals(2, errors.size)
    }

    @Test
    fun testNonxactOrderedRawMatch() {
        val testCalls = listOf(
            CallCentre.Call("proxy1", "call1", arrayOf("hello")),
            CallCentre.Call("proxy3", "call1", arrayOf("hello")),
            CallCentre.Call("proxy1", "call2", arrayOf("hello")))
        val matchCalls = listOf(
            CallCentre.Call("proxy1", "call1", arrayOf("hello")),
            CallCentre.Call("proxy1", "call2", arrayOf("hello")))
        val errors = verifier.matchOrdered(testCalls, matchCalls)
        assertEquals(0, errors.size)
    }

    @Test
    fun testNonexactOrderedFailRawMatch() {
        val testCalls = listOf(
            CallCentre.Call("proxy1", "call1", arrayOf("hello")),
            CallCentre.Call("proxy3", "call1", arrayOf("hello")),
            CallCentre.Call("proxy1", "call2", arrayOf("hello")))
        val matchCalls = listOf(
            CallCentre.Call("proxy1", "call1", arrayOf("hello")),
            CallCentre.Call("proxy4", "call2", arrayOf("hello")))
        val errors = verifier.matchOrdered(testCalls, matchCalls)
        // one pass, 1 fail
        assertEquals(2, errors.size, "expected 2 error got ${errors.size} : ${System.lineSeparator()} ${errors.joinToString (System.lineSeparator())} ")
        val errorsSwitched = verifier.matchOrdered(testCalls, matchCalls)
        // one pass, 1 fail
        assertEquals(2, errorsSwitched.size, "expected 2 error got ${errors.size} : ${System.lineSeparator()} ${errors.joinToString (System.lineSeparator())} ")

    }


}