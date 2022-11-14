package com.typedpath.unittestof.processor

class Model2FactoryMapper {

    class Factory(
        val implementedName: String,
        val implementorName: String,
        val dependencies: List<FactoryClassParam>
    )

    class FactoryResult(
        val factories: MutableMap<String, Factory>,
        val unresolvedPaths: MutableList<MutableList<FactoryClassParam>>
    )

    class FactoryClassParam(val name: String, val type: String)
    class FactoryClass(val packageName: String, val shortName: String, val constructorParams: List<FactoryClassParam>) {
        fun fullName() : String = "$packageName.${shortName}"
    }

    companion object {
        fun asString(p: FactoryClassParam) = """${p.name}:${p.type}"""
        fun asString(f: FactoryClass)  = """${f.packageName}.${f.shortName} => ${f.constructorParams.map { asString(it) }.joinToString (", ")}"""
        fun asString(f: Factory) = """implemented: ${f.implementedName} implementor: ${f.implementorName} dependencies: ${f.dependencies.map { asString(it) }.joinToString (", ")}"""
        fun asString(fr: FactoryResult) =(
"""factories (${fr.factories.size}): 
      ${fr.factories.map { "${it.key}=>${asString(it.value)}" }.joinToString ("${System.lineSeparator()}    ")}
unresolvedPaths (${fr.unresolvedPaths.size}):  
        ${fr.unresolvedPaths.map{ it.map { asString(it) }.joinToString(", ")     }.joinToString ("    ${System.lineSeparator()}")}  """)
    }

    //TODO detect loops
    //TODO deal with class dependencies
    //TODO deal with multiple constructors
    fun getFactories(
        autowiredClasses: List<FactoryClass>,
        interface2Implementer: Map<String, FactoryClass>
    ): FactoryResult {
        println("autowiredClasses: ${autowiredClasses.size}")
        val interface2Factory = mutableMapOf<String, Factory>()
        val unresolvedPaths = mutableListOf<MutableList<FactoryClassParam>>()
        for (autowiredClass in autowiredClasses) {
            val subUnresolvedPaths =
                getFactories(autowiredClass, interface2Implementer, interface2Factory)
            subUnresolvedPaths.forEach {
                val path =
                    mutableListOf<FactoryClassParam>(FactoryClassParam("", "${autowiredClass.packageName}.${autowiredClass.shortName}"))
                path.addAll(it)
                unresolvedPaths.add(path)
            }
        }
        return FactoryResult(factories = interface2Factory, unresolvedPaths = unresolvedPaths)
    }

    private fun getFactories(
        theClass: FactoryClass,
        interface2Implementer: Map<String, FactoryClass>,
        interface2Factory: MutableMap<String, Factory>
    ): MutableList<MutableList<FactoryClassParam>> {
        val unresolvedPaths = mutableListOf<MutableList<FactoryClassParam>>()

        interface2Factory[theClass.fullName()] = Factory(
            implementedName = theClass.fullName(), implementorName = theClass.fullName(),
            dependencies = theClass.constructorParams
        )
        theClass.constructorParams.forEach {
            val param = it
            val subUnresolvedPaths = getFactories(
                strInterface = it.type,
                interface2Implementer = interface2Implementer,
                interface2ExistingFactory = interface2Factory
            )
            subUnresolvedPaths.forEach {
                val fullPath = mutableListOf(param)
                fullPath.addAll(it)
                unresolvedPaths.add(fullPath)
            }
        }

        return unresolvedPaths
    }

    private fun getFactories(
        strInterface: String,
        interface2Implementer: Map<String, FactoryClass>,
        interface2ExistingFactory: MutableMap<String, Factory>
    ): MutableList<MutableList<FactoryClassParam>> {
        val result = mutableListOf<MutableList<FactoryClassParam>>()
        if (interface2ExistingFactory.containsKey(strInterface)) {
            return result
        }
        val implementer = interface2Implementer[strInterface]
        if (null == implementer) {
            result.add(mutableListOf())
            return result
        }
        val dependencies = mutableListOf<FactoryClassParam>()
        val factory = Factory(
            implementorName = implementer.fullName(),
            implementedName = strInterface,
            dependencies = dependencies
        )
        interface2ExistingFactory[strInterface] = factory
        return result
    }

}