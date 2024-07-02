package sampletargetj;        
// Factory generated on Tue Jul 02 09:12:42 BST 2024
//   by com.typedpath.unittestof.processor.TestableRoots2Kotlin
//   for com.typedpath.unittestof.annotationprocessor.UnitTestOfAnnotationProcessor    

import com.typedpath.unittestof.testutil.CallCentre;
import com.typedpath.unittestof.testutil.CallCentre.Match;
import com.typedpath.unittestof.testutil.CallCentre.MatchRow;
public class IService1ProxyContext extends  com.typedpath.unittestof.testutil.ServiceProxyContext<IService1> {
public IService1ProxyContext(CallCentre callCentre, String id) {
   super(sampletargetj.IService1.class, callCentre, id);
}
    public void whenSayHello(Match<String> name ,   String result) {
       getCallCentre().add(new MatchRow(getId(), "sayHello_String", java.util.Arrays.asList(name), result));
    }
    public void whenSayHello(Match<String> name, Match<String> lastName ,   String result) {
       getCallCentre().add(new MatchRow(getId(), "sayHello_String_String", java.util.Arrays.asList(name, lastName), result));
    }
    public java.util.List<String> captureSayHello(Match<String> name ) {
       return getCallCentre().getArgumentsByProxyIdMethodId(getId(), "sayHello_String").stream().map (it -> (String) it[0] ).collect(java.util.stream.Collectors.toList()); 
    }         

    public static class sayHello_String_StringParams {
    public String name;
    public String lastName;
    public sayHello_String_StringParams (String name, String lastName) {
      this.name=name;
      this.lastName=lastName;    
    }
    } 
    public java.util.List<sayHello_String_StringParams> captureSayHello(Match<String> name, Match<String> lastName ) {
       return getCallCentre().getArgumentsByProxyIdMethodId(getId(), "sayHello_String_String").stream().map (it-> new sayHello_String_StringParams((String) it[0],(String) it[1])  ).collect(java.util.stream.Collectors.toList()); 
    }    
        

}
    