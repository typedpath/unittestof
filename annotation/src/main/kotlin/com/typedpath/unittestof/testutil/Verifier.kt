package com.typedpath.unittestof.testutil

import kotlin.math.max

class Verifier : IVerifier {
    private fun string(call: CallCentre.Call?): String =
        if (call == null) "null" else "${call.proxyId}/${call.methodId} (${
            call.args.joinToString(",") { "$it" }
        })"

    override fun matchExactUnordered(testCalls: List<CallCentre.Call>, matchCalls: List<CallCentre.Call>): List<String> {
        return matchUnordered(testCalls, matchCalls, true)
    }


  private fun matchUnordered(testCalls: List<CallCentre.Call>, matchCalls: List<CallCentre.Call>, exact: Boolean = true): List<String> {
        val result = mutableListOf<String>()
        var diffCount = 0
        if (exact && testCalls.size != matchCalls.size) {
            diffCount++
            result.add("callCount: ${testCalls.size} but expected ${matchCalls.size}")
        }
        val unmatchedTestCalls = testCalls.indices.toMutableSet()
        for (m in matchCalls.indices) {
            val matchCall = matchCalls[m]
            var callMatched=false
            for ( t in testCalls.indices ) {
                if (!unmatchedTestCalls.contains(t)) continue
                val diff = testCalls[t] != matchCall
                if (!diff) {
                    callMatched=true
                    unmatchedTestCalls.remove(t)
                    result.add("expected call matched $m ${string(matchCall)}")
                    break
                }
            }
            if (!callMatched) {
                result.add("expected call not matched $m expected call:${string(matchCall)}")
                diffCount++
            }
//                result.add("${i} ${if (diff) "FAIL" else "PASS"} expected call:${string(expectedCall)} actualCall: ${string(actualCall)}")
        }
        unmatchedTestCalls.forEach { result.add("test call not matched $it expected call:${string(testCalls[it])}") }

        return if (diffCount == 0) emptyList() else result
    }

    override fun matchExactOrdered(testCalls: List<CallCentre.Call>, matchCalls: List<CallCentre.Call>): List<String> {
        val result = mutableListOf<String>()
        var diffCount = 0
        if (testCalls.size != matchCalls.size) {
            diffCount++
            result.add("callCount: ${testCalls.size} but expected ${matchCalls.size}")
        }
        var i = 0
        while (i < max(testCalls.size, matchCalls.size)) {
            val actualCall = if (i<testCalls.size)  testCalls[i] else null
            val expectedCall = if (i<matchCalls.size) matchCalls[i] else null
            val diff = actualCall == null || expectedCall == null ||  actualCall != expectedCall
            if (diff) diffCount++
            result.add("$i ${if (diff) "FAIL" else "PASS"} expected call:${string(expectedCall)} actualCall: ${string(actualCall)}")
            i++
        }
        return if (diffCount == 0) emptyList() else result
    }

    override fun matchOrdered(testCalls: List<CallCentre.Call>, matchCalls: List<CallCentre.Call>): List<String> {
        val result = mutableListOf<String>()
        var diffCount = 0
        var testIndex = 0
        var matchIndex = 0
        while (matchIndex < matchCalls.size && testIndex<testCalls.size) {
            val actualCall = testCalls[testIndex]
            val matchCall =  matchCalls[matchIndex]
            if (actualCall == matchCall) {
                result.add("PASS matched at index $testIndex callToMatch:${string(matchCall)} actualCall: ${string(actualCall)}")
                matchIndex++
            }
            testIndex++
        }
        while (matchIndex<matchCalls.size) {
            val matchCall = matchCalls[matchIndex]
            diffCount++
            result.add("unmatched requirement at index $matchIndex callToMatch:${string(matchCall)}")
            matchIndex++
        }

        return if (diffCount == 0) emptyList() else result
    }

    override fun matchUnordered(testCalls: List<CallCentre.Call>, matchCalls: List<CallCentre.Call>): List<String> {
           return matchUnordered(testCalls, matchCalls, false)
    }

    }