package com.typedpath.unittestof.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName

object Ksp2ModelMapper {
    fun fullName(name: KSName?) : String {
        return if (name==null) "nonName"
        else "${name.getQualifier()}.${name.getShortName()}"
    }

    fun factoryClass(theClass: KSClassDeclaration) : Model2FactoryMapper.FactoryClass {
        return Model2FactoryMapper.FactoryClass(packageName = theClass.qualifiedName!!.getQualifier(),
            shortName = theClass.qualifiedName!!.getShortName(),
            constructorParams = theClass.primaryConstructor!!.parameters.map {
                Model2FactoryMapper.FactoryClassParam(
                    name = it.name!!.getShortName(),
                    type = fullName(it.type.resolve().declaration.qualifiedName)
                )
            }
        )
    }
}