package com.typedpath.unittestof.processor

import com.typedpath.unittestof.testutil.BeanContext
import com.typedpath.unittestof.testutil.CallCentre
import com.typedpath.unittestof.testutil.ServiceProxyContext
import java.util.*

/*
class Bean1TestContext : BeanContext<Bean1>(){
import testutil.BeanContext

    val service1Proxy = IService1ProxyContext()
    val target = Bean1(service1Proxy.proxy)
    fun setup() : Bean1 {
        return Bean1(service1Proxy.proxy)
    }
}
 */

fun conciseTypeString(type: String) = type.replace("java.lang.", "")
fun conciseTypeCast(type: String) : String {
    val conciseTypeString = conciseTypeString(type)
    return if ("Any" == conciseTypeString) "" else " as $conciseTypeString"
}

val beanContextClassName = BeanContext::class.java.name
val beanContextShortName = BeanContext::class.simpleName
val serviceProxyContextClassname = ServiceProxyContext::class.java.name
val callLogClassName = CallCentre::class.java.name
val callLogClassShortName = CallCentre::class.simpleName
val matcherClassName = CallCentre.Match::class.java.name.replace("$", ".")
val matchRowClassName = CallCentre.MatchRow::class.java.name.replace("$", ".")

val matchRowSimpleClassName = CallCentre.MatchRow::class.java.simpleName


class TestableRoots2Kotlin(private val emitJava: Boolean) {
    fun textContextSource(creator: String, packagename: String, className: String, root: Model2FactoryMapper.FactoryClass) : String {
        return (
"""${header(packagename, creator)}
${import(beanContextClassName)}    
${if (!emitJava) "class $className : ${beanContextShortName}<${root.shortName}>() {" else 
"public class $className extends ${beanContextShortName}<${root.shortName}> {"}
${root.constructorParams.joinToString (System.lineSeparator()){(
        """    ${recorderDeclaration(it)}
    ${serviceDeclaration(it)} 
"""
        )}}    
${if (!emitJava)"    override val target = ${root.shortName}(${root.constructorParams.joinToString(", ") { "${it.name}Recorder.proxy" }})" else 
"""    final ${root.shortName} target= new ${root.shortName}(${root.constructorParams.joinToString(", ") { "${it.name}Recorder.getProxy()" }});
    @Override
    public ${root.shortName} getTarget() {
        return target;
    }
    """
} 
}
    """)
    }

private fun recorderDeclaration(param: Model2FactoryMapper.FactoryClassParam) = if (!emitJava)
    """val ${param.name}Recorder = ${Interface.interfaceName2TestProxyName(param.type)}(callLog, "${param.name}")""" else
    """public final ${Interface.interfaceName2TestProxyName(param.type)} ${param.name}Recorder = new ${Interface.interfaceName2TestProxyName(param.type)}(getCallLog(), "${param.name}");"""
private fun serviceDeclaration(param: Model2FactoryMapper.FactoryClassParam) = if (!emitJava)
    """val ${param.name} = ${param.name}Recorder.proxy""" else
    """public final ${param.type} ${param.name} = ${param.name}Recorder.getProxy(); """

private fun import(target: String) = "import $target${if (emitJava) ";" else ""}"

data class Interface(val packageName: String, val shortName: String, val methods: List<Method>) {
    data class Method(val name: String, val returnType: String, val params: List<Param>) {
        fun methodId() =  "${name}_${
            params.joinToString("_") {
                conciseTypeString(it.type).replace(
                    ".",
                    "_"
                )
            }
        }"
    }
    data class Param(val name: String, val type: String)
    companion object {
        fun interfaceName2TestProxyName(name: String) = "${name}ProxyContext"
    }
}

fun testProxyContextSource(creator: String, packagename: String, className: String, iface: Interface) : String {
        return (
"""${header(packagename, creator)}
${import(callLogClassName)}
${import(matcherClassName)}
${import(matchRowClassName)}
${proxyContextClassDeclaration(iface, className)}
${iface.methods.joinToString(System.lineSeparator()) { textProxyContextMethod(it) }}
${iface.methods.joinToString(System.lineSeparator()) { testProxyContextCaptureMethod(it) }}
}
    """)
    }

private fun proxyContextClassDeclaration(iface: Interface, className: String) : String {
    return if (!emitJava) {
"""@Suppress("unused")
class ${className}(callCentre: ${callLogClassShortName}, id: String) : ${serviceProxyContextClassname}<${iface.shortName}>(${iface.packageName}.${iface.shortName}::class, callCentre, id) {"""
    } else (
"""public class $className extends  ${serviceProxyContextClassname}<${iface.shortName}> {
public ${className}(${callLogClassShortName} callCentre, String id) {
   super(${iface.packageName}.${iface.shortName}.class, callCentre, id);
}""")
}

private fun textProxyContextMethod(method: Interface.Method) = if (!emitJava)
"""    @Suppress("UNUSED_PARAMETER")
    fun when${method.name.replaceFirstChar { it.titlecase() }}(${
    method.params.joinToString(
        separator = ", "
    ) { textProxyContextParam(it) }
} ${if (method.params.isNotEmpty()) ", " else ""} result: ${method.returnType.replace("java.lang.", "")}) {
       callCentre.add(${matchRowSimpleClassName}(id, "${method.methodId()}", listOf(${
    method.params.joinToString(
        ", "
    ) { it.name }
}), result))
    }
""" else
"""    public void when${method.name.replaceFirstChar { it.titlecase() }}(${
    method.params.joinToString(
        separator = ", "
    ) { textProxyContextParam(it) }
} ${if (method.params.isNotEmpty()) ", " else ""}  ${method.returnType.replace("java.lang.", "")} result) {
       getCallCentre().add(new ${matchRowSimpleClassName}(getId(), "${method.methodId()}", java.util.Arrays.asList(${
    method.params.joinToString(
        ", "
    ) { it.name }
}), result));
    }"""

private fun customCaptureTypeName(method: Interface.Method) = "${method.methodId()}Params"


private fun customCaptureType(method: Interface.Method) = if (!emitJava) "data class ${customCaptureTypeName(method)}( ${
    method.params.joinToString(", ") {
        "val ${it.name}: ${conciseTypeString(it.type)}"
    }
} )" else
"""public static class ${customCaptureTypeName(method)} {
${
    method.params.joinToString(System.lineSeparator()) {
        "    public ${conciseTypeString(it.type)} ${it.name};"
    }
}
    public ${customCaptureTypeName(method)} (${
    method.params.joinToString(", ") {
        "${conciseTypeString(it.type)} ${it.name}"
    }
}) {
${
    method.params.joinToString(System.lineSeparator()) {
        "      this.${it.name}=${it.name};"
    }
}    
    }
    }"""

private fun testProxyContextCaptureMethod(method: Interface.Method) : String {
return (
"""    ${if (method.params.isEmpty()) testProxyContextCaptureMethodZeroParam(method) else if (method.params.size>1) testProxyContextCaptureMethodMultiParam(method) else testProxyContextCaptureMethodSingleParam(method)}        
""")}

private fun testProxyContextCaptureMethodMultiParam(method: Interface.Method) : String {
        val captureType = customCaptureTypeName(method)
        return if (!emitJava)
 """${customCaptureType(method)}        
    @Suppress("UNUSED_PARAMETER")
    fun capture${method.name.replaceFirstChar { it.titlecase() }}(${
     method.params.joinToString(
         separator = ", "
     ) { textProxyContextParam(it) }
 } ) : List<${captureType}> {
       return callCentre.getArgumentsByProxyIdMethodId(id, "${method.methodId()}").map { ${captureType}(${
           method.params.mapIndexed { i, a -> "it[$i] ${conciseTypeCast( a.type)}"  }.joinToString(",")})  } 
    }
""" else
"""${customCaptureType(method)} 
    public java.util.List<${captureType}> capture${method.name.replaceFirstChar { it.titlecase() }}(${
    method.params.joinToString(
        separator = ", "
    ) { textProxyContextParam(it) }
} ) {
       return getCallCentre().getArgumentsByProxyIdMethodId(getId(), "${method.methodId()}").stream().map (it-> new ${captureType}(${
    method.params.mapIndexed { i, a -> "(${conciseTypeString( a.type)}) it[$i]"  }.joinToString(",")})  ).collect(java.util.stream.Collectors.toList()); 
    }    
"""}

private fun testProxyContextCaptureMethodSingleParam(method: Interface.Method) : String {
 return if(!emitJava)
"""    @Suppress("UNUSED_PARAMETER")
    fun capture${method.name.replaceFirstChar { it.titlecase() }}(${
    method.params.joinToString(
        separator = ", "
    ) { textProxyContextParam(it) }
} ) : List<${conciseTypeString( method.params[0].type)}> {
       return callCentre.getArgumentsByProxyIdMethodId(id, "${method.methodId()}").map { it[0] ${conciseTypeCast( method.params[0].type)}  } 
    }     
""" else
"""public java.util.List<${conciseTypeString( method.params[0].type)}> capture${method.name.replaceFirstChar { it.titlecase() }}(${
    method.params.joinToString(
        separator = ", "
    ) { textProxyContextParam(it) }
} ) {
       return getCallCentre().getArgumentsByProxyIdMethodId(getId(), "${method.methodId()}").stream().map (it -> (${conciseTypeString( method.params[0].type)}) it[0] ).collect(java.util.stream.Collectors.toList()); 
    } """
}

private fun testProxyContextCaptureMethodZeroParam(method: Interface.Method) : String {
        return if (!emitJava)
"""    @Suppress("UNUSED_PARAMETER")
    fun capture${method.name.replaceFirstChar { it.titlecase() }}(${
    method.params.joinToString(
        separator = ", "
    ) { textProxyContextParam(it) }
} ) : List<Any> {
       return callCentre.getArgumentsByProxyIdMethodId(id, "${method.methodId()}").map { listOf<Any>()  } 
    }     
""" else
"""java.util.List<Object> capture${method.name.replaceFirstChar { it.titlecase() }}(${
    method.params.joinToString(
        separator = ", "
    ) { textProxyContextParam(it) }
} ) : List<Any> {
       return getCallCentre.getArgumentsByProxyIdMethodId(getId(), "${method.methodId()}").map { java.util.Arrays.asList()  } 
    } """
    }

private fun textProxyContextParam(param: Interface.Param) = if (!emitJava)
        """${param.name}: Match<${conciseTypeString(param.type)}>"""
    else """Match<${conciseTypeString(param.type)}> ${param.name}"""

private fun header(packagename: String, creator: String) = (
"""${if (packagename.trim().isEmpty()) "" else "package $packagename${if (emitJava) ";" else ""}"}        
// Factory generated on ${Date()}
//   by ${javaClass.name}
//   for $creator    
"""
        )
}