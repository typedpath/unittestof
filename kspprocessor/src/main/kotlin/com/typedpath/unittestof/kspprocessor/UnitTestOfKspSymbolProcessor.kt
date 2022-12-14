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

    @Suppress("unused")
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
            .flatMap { it ->
                it.constructorParams.map { it.type }
        }.forEach { it ->
                val ifaceDec = resolver.getClassDeclarationByName(resolver.getKSNameFromString(it))!!
            val iface = TestableRoots2Kotlin.Interface(
                packageName = ifaceDec.qualifiedName!!.getQualifier(),
                shortName = ifaceDec.qualifiedName!!.getShortName(),
                methods = ifaceDec.getAllFunctions().map { function ->
                    TestableRoots2Kotlin.Interface.Method(
                    name = function.qualifiedName!!.getShortName(),
                    returnType = fullName(function.returnType!!.resolve().declaration.qualifiedName),
                    params = function.parameters.map { TestableRoots2Kotlin.Interface.Param(name = it.name!!.getShortName(),
                        type=it.type.toString()) }
                ) }.toList())
            name2IFace[fullName(ifaceDec.qualifiedName)] = iface
        }

        name2IFace.forEach { theName ->
            val iface: TestableRoots2Kotlin.Interface = theName.value
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

        producingClassDecs.forEach { classDecl ->
            val packageName = classDecl.packageName
            val className = "${classDecl.shortName}TestContext"

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
                classDecl
            )
            fileKt.bufferedWriter().use { it.write(strKotlin) }
            fileKt.flush()
        }
        return emptyList()
    }

}