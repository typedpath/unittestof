package com.typedpath.unittestof.kspprocessor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class UnitTestOfKspSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
       environment.logger.warn("*********** UnitTestOfKspSymbolProcessorProvider create")
             return UnitTestOfKspSymbolProcessor(environment)
    }
}