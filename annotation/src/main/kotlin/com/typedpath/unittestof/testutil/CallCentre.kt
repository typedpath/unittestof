package com.typedpath.unittestof.testutil

import java.lang.reflect.Method

/*
needs to contain matches and recorded logs
CallLogAndMatch CallCentre
 */
class CallCentre {
    class Match<T>(val match: (t: T)->Boolean) {
        @Suppress("unused")
        constructor(t: T) : this ( { tin ->  t==null && tin==null || t!=null && t == tin})
        companion object {
            @JvmStatic
            fun <T> any() = Match<T> { true }
        }
        @SuppressWarnings("unchecked")
        fun matchUntyped(o: Any?) : Boolean {
            return try {
                match(o as T)
            } catch (ex: Exception) {
                false
            }
        }
    }
    var skipMatch = false

    class MatchRow(val proxyId: String, val strMethod: String, val matchers: List<Match<*>>, val result: Any?) {
        companion object {
            private fun normalizeTypeName(name: String) = name.replace("java.lang.", "")

            fun methodId(m: Method) =
                "${m.name}_${m.parameterTypes.joinToString("_"){ normalizeTypeName(it.name) }}"
        }
        fun matches(proxyIdIn: String, methodIdIn: String, args: Array<Any>) : Boolean =
            proxyIdIn == proxyId && methodIdIn == strMethod &&
                    !matchers.indices.any { !matchers[it].matchUntyped(args[it]) }
    }

    private val matchRows = mutableListOf<MatchRow>()
    @Suppress("unused")
    fun add(matchRow: MatchRow) = matchRows.add(matchRow)

    fun match(proxyId: String, methodId: String, args: Array<Any>) : Any? {
        if (skipMatch) return null
        val matchRow = matchRows.find { it.matches(proxyId, methodId, args)  }
        matchRow?.let{ println("matched ${proxyId}.${methodId} => ${it.result}")}?:
        error(
            "unmatched call to ${proxyId}.${methodId}(${args.joinToString(","){""+it }}) in ${
                matchRows.joinToString(",") {
                    it.strMethod
                }
            }")
        return matchRow.result
    }

    class Call(val proxyId: String, val methodId: String, val args: Array<Any>) {

        override fun equals(other: Any?): Boolean {
            if (other !is Call) {
                return false
            }
            if (proxyId != other.proxyId) return false
            if (methodId != other.methodId) return false
            if (args.size!=other.args.size) {
                return false
            }

            for (i in args.indices) {
                if (args[i] !=  other.args[i]) return false
            }
            return true
        }

    }
    private var _calls = mutableListOf<Call>()
    var calls: List<Call>
        get() = _calls
        set(value) {
            _calls = value.toMutableList()
        }

    fun addCall(proxyId: String, methodId: String, args: Array<Any>) = _calls.add(Call(proxyId, methodId, args))

    fun getArgumentsByProxyIdMethodId(proxyId: String, methodId: String) : List<Array<Any>> {
        val key = "$proxyId/$methodId"
        return _calls.filter { "${it.proxyId}/${it.methodId}" == key }.map {it.args}
    }

}

