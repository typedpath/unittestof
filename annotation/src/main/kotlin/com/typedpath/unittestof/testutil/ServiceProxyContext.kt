package com.typedpath.unittestof.testutil

import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass

open class ServiceProxyContext<T>(kclass: KClass<*>, val callCentre: CallCentre, val id: String) {

    constructor(clazz: Class<*>,  callCentre: CallCentre, id: String) : this(Reflection.createKotlinClass(clazz), callCentre, id)

    val methodId2Arguments = mutableMapOf<String, MutableList<Array<Any>>>()
    fun getArgumentsByMethodId(methodId: String) : List<Array<Any>> {
        var arguments = methodId2Arguments.get(methodId)
        if (arguments==null) {
            arguments =  mutableListOf<Array<Any>>()
            methodId2Arguments.put(methodId, arguments)
        }
        return arguments
    }

    val dynamicInvocationHandler = { _: Any, method: Method, args: Array<Any>  ->
        val methodId = CallCentre.MatchRow.methodId(method)
        callCentre.addCall(id, methodId, args)
        callCentre.match(id, methodId, args)
    }

    @Suppress("unchecked")
    val proxy = Proxy.newProxyInstance(
       kclass.java.classLoader,  arrayOf<Class<*>>(kclass.java), dynamicInvocationHandler) as T

    val methods = kclass.java.methods
}

fun <T> verify(context: ServiceProxyContext<T>, block: T.() ->Unit) : List<String> {
    TODO() // proxy based verify
}
