package com.typedpath.unittestof.testutil

interface IVerifier {
    fun matchExactUnordered(
        testCalls: List<CallCentre.Call>,
        matchCalls: List<CallCentre.Call>
    ): List<String>

    fun matchExactOrdered(
        testCalls: List<CallCentre.Call>,
        matchCalls: List<CallCentre.Call>
    ): List<String>

    fun matchOrdered(
        testCalls: List<CallCentre.Call>,
        matchCalls: List<CallCentre.Call>
    ): List<String>

    fun matchUnordered(
        testCalls: List<CallCentre.Call>,
        matchCalls: List<CallCentre.Call>
    ): List<String>
}