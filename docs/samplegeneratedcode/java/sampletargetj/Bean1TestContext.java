package sampletargetj;        
// Factory generated on Tue Jul 02 09:12:42 BST 2024
//   by com.typedpath.unittestof.processor.TestableRoots2Kotlin
//   for com.typedpath.unittestof.annotationprocessor.UnitTestOfAnnotationProcessor    

import com.typedpath.unittestof.testutil.BeanContext;    
public class Bean1TestContext extends BeanContext<Bean1> {
    public final sampletargetj.IService1ProxyContext service1Recorder = new sampletargetj.IService1ProxyContext(getCallLog(), "service1");
    public final sampletargetj.IService1 service1 = service1Recorder.getProxy();  
    
    final Bean1 target= new Bean1(service1Recorder.getProxy());
    @Override
    public Bean1 getTarget() {
        return target;
    }
     
}
    