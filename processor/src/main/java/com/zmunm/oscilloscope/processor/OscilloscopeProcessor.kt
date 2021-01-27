package com.zmunm.oscilloscope.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import com.zmunm.oscilloscope.OscilloscopeEvent
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class OscilloscopeProcessor : AbstractProcessor() {

    companion object {
        private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        private val fileBuilder =
            FileSpec.builder("com.zmunm.generated", "OscilloscopeExtension")
    }

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(
        OscilloscopeEvent::class.java.name
    )

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        val fieldElements = roundEnv.getElementsAnnotatedWith(OscilloscopeEvent::class.java)

        if (!checkElementType(ElementKind.FIELD, fieldElements)) return false

        fieldElements.forEach {
            fileBuilder.addFunction(makeValidateFunction(it))
        }

        processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]?.let { kaptKotlinGeneratedDir ->
            fileBuilder.build().writeTo(File(kaptKotlinGeneratedDir))
        }
        return true
    }

    private fun makeValidateFunction(fieldElements: Element): FunSpec {
        return FunSpec.builder("validate")
            .receiver(fieldElements.asType().asTypeName())
            .returns(Boolean::class)
            .addStatement("val result: %T = false", Boolean::class.java)
            .addStatement("return result", Boolean::class.java)
            .build()
    }

    private fun checkElementType(kind: ElementKind, elements: Set<Element>): Boolean {
        if (elements.isEmpty()) return false

        elements.forEach {
            if (it.kind != kind) {
                printMessage(
                    Diagnostic.Kind.ERROR, "Only ${kind.name} Are Supported", it
                )
                return false
            }
        }
        return true
    }

    private fun printMessage(kind: Diagnostic.Kind, message: String, element: Element) {
        processingEnv.messager.printMessage(kind, message, element)
    }
}
