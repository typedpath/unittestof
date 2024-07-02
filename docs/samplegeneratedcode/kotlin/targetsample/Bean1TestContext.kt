package targetsample        
// Factory generated on Tue Jul 02 09:08:31 BST 2024
//   by com.typedpath.unittestof.processor.TestableRoots2Kotlin
//   for UnitTestOfKspSymbolProcessor    

import com.typedpath.unittestof.testutil.BeanContext    
class Bean1TestContext : BeanContext<Bean1>() {
    val service1Recorder = targetsample.IService1ProxyContext(callLog, "service1")
    val service1 = service1Recorder.proxy 
    
    override val target = Bean1(service1Recorder.proxy) 
}
    