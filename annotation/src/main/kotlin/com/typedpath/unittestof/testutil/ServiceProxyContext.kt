package com.typedpath.unittestof.testutil

import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass

open class ServiceProxyContext<T>(kclass: KClass<*>, val callCentre: CallCentre, val id: String) {

    constructor(clazz: Class<*>,  callCentre: CallCentre, id: String) : this(Reflection.createKotlinClass(clazz), callCentre, id)

    private val methodId2Arguments = mutableMapOf<String, MutableList<Array<Any>>>()

    private val dynamicInvocationHandler = { _: Any, method: Method, args: Array<Any>  ->
        val methodId = CallCentre.MatchRow.methodId(method)
        callCentre.addCall(id, methodId, args)
        callCentre.match(id, methodId, args)
    }

    @Suppress("unchecked")
    val proxy = Proxy.newProxyInstance(
       kclass.java.classLoader,  arrayOf<Class<*>>(kclass.java), dynamicInvocationHandler) as T
}

fun <T> verify(context: ServiceProxyContext<T>, block: T.() ->Unit) : List<String> {
    TODO() // proxy based verify
}
