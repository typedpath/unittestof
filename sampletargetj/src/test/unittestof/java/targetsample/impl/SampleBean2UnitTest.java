package targetsample.impl;

import static java.util.Arrays.asList;

import java.util.Arrays;

import com.typedpath.unittestof.annotation.UnitTest;
import com.typedpath.unittestof.annotation.UnitTestOf;
import com.typedpath.unittestof.testutil.CallCentre.Match;
import static com.typedpath.unittestof.testutil.CallCentre.Match.any;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sampletargetj.Bean2;
import sampletargetj.Bean2TestContext;
import sampletargetj.IService1ProxyContext;
import sampletargetj.IService2ProxyContext;
import sampletargetj.IService3ProxyContext;
import sampletargetj.Pair;


@UnitTest
public class SampleBean2UnitTest implements UnitTestOf<Bean2> {

    Bean2TestContext context;

    @BeforeEach
    public void setup() {
        context = new Bean2TestContext();
    }

    @Test
    public void testProxyExactArgMatch() {
        context.service1Recorder.whenSayHello(new Match("hi"), "hello1");
        context.service2Recorder.whenSayHello2(new Match("hi"), "hello2");
        context.service3Recorder.whenSayHello3(new Match("hi"), "hello3");
        String greeting = context.getTarget().greet(asList("hi"));
        assertEquals("hello1,hello2,hello3", greeting);
    }

    @Test
    //verify params sent to services with cap
    public void testLooseArgMatchingVerifyCallsWithCaptured() {
        context.service1Recorder.whenSayHello(Match.any(), any(), "tweeet1");
        context.service2Recorder.whenSayHello2(Match.any(), Match.any(), "tweeet2");
        context.service3Recorder.whenSayHello3(Match.any(), Match.any(), "tweeet3");
        String greeting = context.getTarget().officialGreet(Arrays.asList(new Pair<>("Mr", "Pigeon")));
        assertEquals("tweeet1,tweeet2,tweeet3", greeting );
        java.util.List<IService1ProxyContext.sayHello_String_StringParams> sayHelloCaptor1 = context.service1Recorder.captureSayHello(any(), any());
        assertEquals(sayHelloCaptor1.get(0).name, "Mr");
        assertEquals(sayHelloCaptor1.get(0).lastName, "Pigeon");
        java.util.List<IService2ProxyContext.sayHello2_String_StringParams> sayHelloCaptor2 = context.service2Recorder.captureSayHello2(any(), any());
        assertEquals(sayHelloCaptor2.get(0).name, "Mr");
        assertEquals(sayHelloCaptor2.get(0).lastName, "Pigeon");
        java.util.List<IService3ProxyContext.sayHello3_String_StringParams> sayHelloCaptor3 = context.service3Recorder.captureSayHello3(any(), any());
        assertEquals(sayHelloCaptor3.get(0).name, "Mr");
        assertEquals(sayHelloCaptor3.get(0).lastName, "Pigeon");
    }

}
