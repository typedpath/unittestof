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

    @Suppress("unused")
    companion object {
        private fun asString(p: FactoryClassParam) = """${p.name}:${p.type}"""
        fun asString(f: FactoryClass)  = """${f.packageName}.${f.shortName} => ${f.constructorParams.joinToString (", "){ asString(it) }}"""
        private fun asString(f: Factory) = """implemented: ${f.implementedName} implementor: ${f.implementorName} dependencies: ${f.dependencies.joinToString (", "){ asString(it) }}"""
        fun asString(fr: FactoryResult) =(
"""factories (${fr.factories.size}): 
      ${fr.factories.map { "${it.key}=>${asString(it.value)}" }.joinToString ("${System.lineSeparator()}    ")}
unresolvedPaths (${fr.unresolvedPaths.size}):  
        ${
    fr.unresolvedPaths.joinToString("    ${System.lineSeparator()}") { path ->
        path.joinToString(", ") {
            asString(
                it
            )
        }
    }
}  """)
    }

}