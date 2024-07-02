package sampletargetj;        
// Factory generated on Tue Jul 02 09:12:42 BST 2024
//   by com.typedpath.unittestof.processor.TestableRoots2Kotlin
//   for com.typedpath.unittestof.annotationprocessor.UnitTestOfAnnotationProcessor    

import com.typedpath.unittestof.testutil.BeanContext;    
public class Bean2TestContext extends BeanContext<Bean2> {
    public final sampletargetj.IService1ProxyContext service1Recorder = new sampletargetj.IService1ProxyContext(getCallLog(), "service1");
    public final sampletargetj.IService1 service1 = service1Recorder.getProxy();  

    public final sampletargetj.IService2ProxyContext service2Recorder = new sampletargetj.IService2ProxyContext(getCallLog(), "service2");
    public final sampletargetj.IService2 service2 = service2Recorder.getProxy();  

    public final sampletargetj.IService3ProxyContext service3Recorder = new sampletargetj.IService3ProxyContext(getCallLog(), "service3");
    public final sampletargetj.IService3 service3 = service3Recorder.getProxy();  
    
    final Bean2 target= new Bean2(service1Recorder.getProxy(), service2Recorder.getProxy(), service3Recorder.getProxy());
    @Override
    public Bean2 getTarget() {
        return target;
    }
     
}
    