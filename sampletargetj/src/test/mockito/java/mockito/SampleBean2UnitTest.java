package mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sampletargetj.Bean1;
import sampletargetj.Bean2;
import sampletargetj.IService1;
import sampletargetj.IService2;
import sampletargetj.IService3;
import sampletargetj.Pair;

public class SampleBean2UnitTest {

    @Mock
    IService1 service1;
    @Mock
    IService2 service2;
    @Mock
    IService3 service3;

    @InjectMocks
    Bean2 bean2;

    @Captor
    ArgumentCaptor<String> sayHello1Arg0Captor;

    @Captor
    ArgumentCaptor<String> sayHello1Arg1Captor;

    @Captor
    ArgumentCaptor<String> sayHello2Arg0Captor;

    @Captor
    ArgumentCaptor<String> sayHello2Arg1Captor;

    @Captor
    ArgumentCaptor<String> sayHello3Arg0Captor;

    @Captor
    ArgumentCaptor<String> sayHello3Arg1Captor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProxyExactArgMatch() {
        when(service1.sayHello("hi")).thenReturn("hello1");
        when(service2.sayHello2("hi")).thenReturn("hello2");
        when(service3.sayHello3("hi")).thenReturn("hello3");
        String greeting = bean2.greet(Arrays.asList("hi"));
        assertEquals("hello1,hello2,hello3", greeting);
    }

    @Test
    //verify params sent to services with cap
    public void testLooseArgMatchingVerifyCallsWithCaptured() {
        when(service1.sayHello(any(), any())).thenReturn("tweeet1");
        when(service2.sayHello2(any(), any())).thenReturn("tweeet2");
        when(service3.sayHello3(any(), any())).thenReturn("tweeet3");
        String greeting = bean2.officialGreet(Arrays.asList(new Pair<>("Mr", "Pigeon")));
        assertEquals("tweeet1,tweeet2,tweeet3", greeting );
        verify(service1).sayHello(sayHello1Arg0Captor.capture(), sayHello1Arg1Captor.capture());
        assertEquals(sayHello1Arg0Captor.getValue(), "Mr");
        assertEquals(sayHello1Arg1Captor.getValue(), "Pigeon");
        verify(service2).sayHello2(sayHello2Arg0Captor.capture(), sayHello2Arg1Captor.capture());
        assertEquals(sayHello2Arg0Captor.getValue(), "Mr");
        assertEquals(sayHello2Arg1Captor.getValue(), "Pigeon");
        verify(service3).sayHello3(sayHello3Arg0Captor.capture(), sayHello3Arg1Captor.capture());
        assertEquals(sayHello3Arg0Captor.getValue(), "Mr");
        assertEquals(sayHello3Arg1Captor.getValue(), "Pigeon");
    }

}
