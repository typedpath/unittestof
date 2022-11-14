package targetsample.impl;


import static com.typedpath.unittestof.testutil.BeanContext.verifyJ;
import static com.typedpath.unittestof.testutil.CallCentre.Match.any;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.typedpath.unittestof.annotation.UnitTest;
import com.typedpath.unittestof.annotation.UnitTestOf;
import com.typedpath.unittestof.testutil.CallCentre.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sampletargetj.Bean1;
import sampletargetj.Bean1TestContext;
import sampletargetj.IService1ProxyContext;
import sampletargetj.Pair;


@UnitTest
public class SampleBean1UnitTest implements UnitTestOf<Bean1> {

    Bean1TestContext context;

    @BeforeEach
    public void setup() {
        context = new Bean1TestContext();
    }

    @Test
    public void testProxyExactArgMatch() {
        context.service1Recorder.whenSayHello(new Match("hi"), "hello");
        String greeting = context.getTarget().greet(asList("hi"));
        assertEquals("hello", greeting);
    }

    @Test
//verify params sent to services with cap
    public void testLooseArgMatchingVerifyCallsWithCaptured() {
        context.service1Recorder.whenSayHello(any(), any(), "tweeet");
        String greeting = context.getTarget().officialGreet(asList(new Pair<>("Mr", "Pigeon")));
        assertEquals(greeting, "tweeet");
        List<IService1ProxyContext.sayHello_String_StringParams> sayHelloParams = context.service1Recorder.captureSayHello(any(), any());
        assertEquals("Mr", sayHelloParams.get(0).name);
        assertEquals("Pigeon", sayHelloParams.get(0).lastName);
    }

}