package sampletargetj;        
// Factory generated on Tue Jul 02 09:12:42 BST 2024
//   by com.typedpath.unittestof.processor.TestableRoots2Kotlin
//   for com.typedpath.unittestof.annotationprocessor.UnitTestOfAnnotationProcessor    

import com.typedpath.unittestof.testutil.CallCentre;
import com.typedpath.unittestof.testutil.CallCentre.Match;
import com.typedpath.unittestof.testutil.CallCentre.MatchRow;
public class IService2ProxyContext extends  com.typedpath.unittestof.testutil.ServiceProxyContext<IService2> {
public IService2ProxyContext(CallCentre callCentre, String id) {
   super(sampletargetj.IService2.class, callCentre, id);
}
    public void whenSayHello2(Match<String> name ,   String result) {
       getCallCentre().add(new MatchRow(getId(), "sayHello2_String", java.util.Arrays.asList(name), result));
    }
    public void whenSayHello2(Match<String> name, Match<String> lastName ,   String result) {
       getCallCentre().add(new MatchRow(getId(), "sayHello2_String_String", java.util.Arrays.asList(name, lastName), result));
    }
    public java.util.List<String> captureSayHello2(Match<String> name ) {
       return getCallCentre().getArgumentsByProxyIdMethodId(getId(), "sayHello2_String").stream().map (it -> (String) it[0] ).collect(java.util.stream.Collectors.toList()); 
    }         

    public static class sayHello2_String_StringParams {
    public String name;
    public String lastName;
    public sayHello2_String_StringParams (String name, String lastName) {
      this.name=name;
      this.lastName=lastName;    
    }
    } 
    public java.util.List<sayHello2_String_StringParams> captureSayHello2(Match<String> name, Match<String> lastName ) {
       return getCallCentre().getArgumentsByProxyIdMethodId(getId(), "sayHello2_String_String").stream().map (it-> new sayHello2_String_StringParams((String) it[0],(String) it[1])  ).collect(java.util.stream.Collectors.toList()); 
    }    
        

}
    