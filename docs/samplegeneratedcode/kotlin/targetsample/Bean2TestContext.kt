package targetsample        
// Factory generated on Tue Jul 02 09:08:31 BST 2024
//   by com.typedpath.unittestof.processor.TestableRoots2Kotlin
//   for UnitTestOfKspSymbolProcessor    

import com.typedpath.unittestof.testutil.BeanContext    
class Bean2TestContext : BeanContext<Bean2>() {
    val service1Recorder = targetsample.IService1ProxyContext(callLog, "service1")
    val service1 = service1Recorder.proxy 

    val service2Recorder = targetsample.IService2ProxyContext(callLog, "service2")
    val service2 = service2Recorder.proxy 
    
    override val target = Bean2(service1Recorder.proxy, service2Recorder.proxy) 
}
    