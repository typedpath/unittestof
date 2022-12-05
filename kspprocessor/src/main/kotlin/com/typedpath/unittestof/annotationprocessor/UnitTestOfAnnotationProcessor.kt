package com.typedpath.unittestof.annotationprocessor

import com.typedpath.unittestof.annotation.UnitTest
import com.typedpath.unittestof.annotation.UnitTestOf
import com.typedpath.unittestof.processor.Model2FactoryMapper
import com.typedpath.unittestof.processor.TestableRoots2Kotlin
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

var info =false
class UnitTestOfAnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val EMIT_JAVA_OPTION_NAME = "UnitTestOf_emitJava"
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME_DESCRIPTION = "target directory for generated kotlin source"
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        roundEnv!! // must be nonnull surely

        info( "******* UnitTestOf here x!")

        val testTargets: Set<TypeMirror> = roundEnv.rootElements
            .filter { it.kind == ElementKind.CLASS }
            .map { it as TypeElement }
            .flatMap { it.interfaces }
            .filterIsInstance<DeclaredType>()
            .filter { UnitTestOf::class.java.name.equals(fullName(it.asElement())) }
            .flatMap { it.typeArguments }
            .toSet()

        val emitJava = processingEnv.options.containsKey(EMIT_JAVA_OPTION_NAME) && "true" == processingEnv.options[EMIT_JAVA_OPTION_NAME]
            info(
                "option $EMIT_JAVA_OPTION_NAME=true in progress **************** emitJava:${emitJava} from ${processingEnv.options[EMIT_JAVA_OPTION_NAME]}"
            )

        val rootDirectory = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        if (!emitJava && rootDirectory == null) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                """option $KAPT_KOTLIN_GENERATED_OPTION_NAME ($KAPT_KOTLIN_GENERATED_OPTION_NAME_DESCRIPTION) is required not found in 
                   ${processingEnv.options.map { "${it.key}=>${it.value}${System.lineSeparator()}" }}  
                """
            )
            return true
        }

        val creator = UnitTestOfAnnotationProcessor::class.java.name

        testTargets.forEach {
            val classElement = (it as DeclaredType).asElement() as TypeElement
            val packageName =
                processingEnv.elementUtils.getPackageOf(classElement).qualifiedName.toString()
            val className = "${classElement.simpleName}TestContext"
            val factoryClass = asFactoryClass(classElement)
            //should be all contructor params from the target
            val root = Model2FactoryMapper.FactoryClass(
                packageName,
                classElement.simpleName.toString(),
                factoryClass.constructorParams
            )

            val src =
                (TestableRoots2Kotlin(emitJava)).textContextSource(creator, packageName, className, root)

            writeSource(rootDirectory = rootDirectory, packageName=packageName, className=className, src=src, emitJava=emitJava)

        }

        val dependantInterfaces = mutableMapOf<String, TestableRoots2Kotlin.Interface>()
        testTargets.forEach { target ->
            dependantInterfacesOfClass((target as DeclaredType).asElement()).forEach {
                dependantInterfaces[it.key] = it.value
            }
        }

        dependantInterfaces.forEach {
            val className = TestableRoots2Kotlin.Interface.interfaceName2TestProxyName(it.value.shortName)
            val src = (TestableRoots2Kotlin(emitJava)).testProxyContextSource(creator = creator, packagename = it.value.packageName,
                className = className, iface = it.value)
            writeSource(rootDirectory=rootDirectory, packageName = it.value.packageName, className=className, src = src, emitJava=emitJava)
        }

        return true
    }

    private fun writeSource(rootDirectory: String?, packageName: String, className: String, src: String, emitJava: Boolean) {
       info(
            """writing source rootDirectory: $rootDirectory, packageName: $packageName, className: $className, src: String, emitJava: $emitJava"""
        )
        if (!emitJava) {
            //TODO fix this
            rootDirectory!!
            val fullFilename = "$rootDirectory${
                if (rootDirectory.endsWith(File.separatorChar)) "" else File.separatorChar
            }${
                packageName.replace('.', File.separatorChar)
            }${File.separatorChar}${className}.kt"
            val sourceFile = File(fullFilename)
            sourceFile.parentFile.mkdirs()
            sourceFile.writeText(src)
        } else {
            processingEnv.filer.createSourceFile("$packageName.$className").openWriter().use {
                  it.write(src)
            }
        }

    }

    @Suppress("unused")
    private fun printEl(element: Element, margin: String ="") {
        warn("$margin $element ${element.enclosingElement}")
        element.enclosedElements.forEach { printEl(it, "$margin ") }
    }

    private fun fullName(el: Element) =  "${processingEnv.elementUtils.getPackageOf(el).qualifiedName}.${el.simpleName}"

    @Suppress("unused")
    fun interfaces(element: Element) : List<Element> =
        processingEnv.typeUtils.directSupertypes(element.asType()).map{
            this.processingEnv.typeUtils.asElement(it)
        }.filter { it.kind==ElementKind.INTERFACE }


    private fun warn(str: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "${UnitTestOfAnnotationProcessor::class.simpleName} $str")
    }

    private fun info(str: String) {
        if (info) processingEnv.messager.printMessage(Diagnostic.Kind.OTHER, "${UnitTestOfAnnotationProcessor::class.simpleName} $str")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(UnitTest::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    private fun dependantInterfacesOfClass(element: Element) :Map<String, TestableRoots2Kotlin.Interface> {
        val constructors = element.enclosedElements.filter { ElementKind.CONSTRUCTOR == it.kind }
            .map {
                it as ExecutableElement
            }
        if (constructors.isEmpty()) {
            return emptyMap()
        }

        fun asMethod(element: ExecutableElement) : TestableRoots2Kotlin.Interface.Method {
            return TestableRoots2Kotlin.Interface.Method( name =  element.simpleName.toString(),
                returnType="${element.returnType}", params = element
                    .parameters.map { TestableRoots2Kotlin.Interface.Param(name = it.simpleName.toString(),
                        type=it.asType().toString()) })
        }

        val result =  constructors.flatMap { it.parameters }.map {
            (it.asType() as DeclaredType).asElement() as TypeElement
        }.associate { paramEl ->
            val fullName = paramEl.qualifiedName.toString()
            val lastDotIndex = fullName.lastIndexOf(".")
            val strPackage = if (lastDotIndex>0) fullName.substring(0, lastDotIndex) else ""
            paramEl.qualifiedName.toString() to
               TestableRoots2Kotlin.Interface(
                      packageName = strPackage,
                      shortName = paramEl.simpleName.toString(),
                      methods =  paramEl.enclosedElements.filter { ElementKind.METHOD == it.kind  }.map { asMethod( it as ExecutableElement) }
            )
        }
        return result
    }

    private fun asFactoryClass(element: Element) : Model2FactoryMapper.FactoryClass {
        val name = element.toString()
        val constructors = element.enclosedElements.filter { ElementKind.CONSTRUCTOR==it.kind }
            .map {
                it as ExecutableElement
            }
        val constructorParameters: List<Model2FactoryMapper.FactoryClassParam> = if (constructors.isEmpty()) mutableListOf()  else
            constructors[0].parameters.map { Model2FactoryMapper.FactoryClassParam(name = it.simpleName.toString(),
                it.asType().toString())
            }
        val lastDotIndex = name.lastIndexOf(".")
        var packageName = ""
        var shortName = name
        if (lastDotIndex>-1) {
            packageName=name.substring(0, lastDotIndex)
            shortName = name.substring(lastDotIndex+1)
        }

        return Model2FactoryMapper.FactoryClass(packageName = packageName, shortName=shortName, constructorParams = constructorParameters)
    }

}