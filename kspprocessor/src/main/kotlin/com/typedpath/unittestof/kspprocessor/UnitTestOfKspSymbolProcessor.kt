package com.typedpath.unittestof.kspprocessor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.typedpath.unittestof.annotation.UnitTestOf
import com.typedpath.unittestof.processor.Ksp2ModelMapper
import com.typedpath.unittestof.processor.Ksp2ModelMapper.fullName
import com.typedpath.unittestof.processor.TestableRoots2Kotlin


class UnitTestOfKspSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private var invoked = false

    fun warn(str: String) = environment.logger.warn("${javaClass.simpleName} $str")

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }
        invoked = true

        val producingClassDecs = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { ClassKind.CLASS == it.classKind}
            .flatMap { it.superTypes }
            .map {it.resolve()}
            .filter { fullName(it.declaration.qualifiedName) == UnitTestOf::class.java.name }
            .flatMap { it.arguments}
            .map {it.type}
            .filterIsInstance<KSTypeReference>()
            .map { fullName(it.resolve().declaration.qualifiedName) }
            .map { Ksp2ModelMapper.factoryClass(resolver.getClassDeclarationByName(resolver.getKSNameFromString(it))!!) }
            .toSet()

        val name2IFace = mutableMapOf<String, TestableRoots2Kotlin.Interface>()
        producingClassDecs
            .onEach { "checkoing for ints: ${it.fullName()}" }
            .flatMap {
            it.constructorParams.map { it.type }
        }.forEach {
            val ifaceDec = resolver.getClassDeclarationByName(resolver.getKSNameFromString(it))!!
            val iface = TestableRoots2Kotlin.Interface(
                packageName = ifaceDec.qualifiedName!!.getQualifier(),
                shortName = ifaceDec.qualifiedName!!.getShortName(),
                methods = ifaceDec.getAllFunctions().map { TestableRoots2Kotlin.Interface.Method(
                    name = it.qualifiedName!!.getShortName(),
                    returnType = fullName(it.returnType!!.resolve().declaration.qualifiedName),
                    params = it.parameters.map { TestableRoots2Kotlin.Interface.Param(name = it.name!!.getShortName(),
                        type=it.type.toString()) }
                ) }.toList())
            name2IFace[fullName(ifaceDec.qualifiedName)] = iface
        }

        name2IFace.forEach {
            val iface: TestableRoots2Kotlin.Interface = it.value
            val className =  TestableRoots2Kotlin.Interface.interfaceName2TestProxyName(iface.shortName)

            val fileKt = environment.codeGenerator.createNewFile(
                Dependencies(false),
                iface.packageName,
                className,
                "kt"
            )
            val strKotlin = TestableRoots2Kotlin(false).testProxyContextSource(
                javaClass.simpleName,
                iface.packageName,
                className,
                iface
            )
            fileKt.bufferedWriter().use { it.write(strKotlin) }
            fileKt.flush()
        }

        producingClassDecs.forEach {
            val packageName = it.packageName
            val className = "${it.shortName}TestContext"

            val fileKt = environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                className,
                "kt"
            )
            val strKotlin = TestableRoots2Kotlin(false).textContextSource(
                javaClass.simpleName,
                packageName,
                className,
                it
            )
            fileKt.bufferedWriter().use { it.write(strKotlin) }
            fileKt.flush()
        }
        return emptyList()
    }

}