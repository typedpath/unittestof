package com.typedpath.unittestof.testutil

class VerifierStub(private val verifyReturn: List<String>) : IVerifier {

    val  callName2Params =  mutableMapOf<String, Pair<List<CallCentre.Call>, List<CallCentre.Call>>>()

    override fun matchExactUnordered(testCalls: List<CallCentre.Call>, matchCalls: List<CallCentre.Call>): List<String> {
        callName2Params[IVerifier::matchExactUnordered.name] = Pair(testCalls, matchCalls)
        return verifyReturn
    }

    override fun matchExactOrdered(testCalls: List<CallCentre.Call>, matchCalls: List<CallCentre.Call>): List<String> {
        callName2Params[IVerifier::matchExactOrdered.name] = Pair(testCalls, matchCalls)
        return verifyReturn
    }

    override fun matchOrdered(testCalls: List<CallCentre.Call>, matchCalls: List<CallCentre.Call>): List<String> {
        callName2Params[IVerifier::matchOrdered.name] = Pair(testCalls, matchCalls)
        return verifyReturn
    }

    override fun matchUnordered(testCalls: List<CallCentre.Call>, matchCalls: List<CallCentre.Call>): List<String> {
        callName2Params[IVerifier::matchUnordered.name] = Pair(testCalls, matchCalls)
        return verifyReturn
    }
}