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
import sampletargetj.Pair;
import sampletargetj.IService1;

public class SampleBean1UnitTest {

    @Mock
    IService1 service1;

    @InjectMocks
    Bean1 bean1;

    @Captor
    ArgumentCaptor<String> sayHelloArg0Captor;

    @Captor
    ArgumentCaptor<String> sayHelloArg1Captor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProxyExactArgMatch() {
        when(service1.sayHello("hi")).thenReturn("hello");
        String greeting = bean1.greet(Arrays.asList("hi"));
        assertEquals("hello", greeting);
    }

    @Test
    //verify params sent to services with cap
    public void testLooseArgMatchingVerifyCallsWithCaptured() {
        when(service1.sayHello(any(), any())).thenReturn("tweeet");
        String greeting = bean1.officialGreet(Arrays.asList(new Pair<>("Mr", "Pigeon")));
        assertEquals(greeting, "tweeet");
        verify(service1).sayHello(sayHelloArg0Captor.capture(), sayHelloArg1Captor.capture());
        assertEquals(sayHelloArg0Captor.getValue(), "Mr");
        assertEquals(sayHelloArg1Captor.getValue(), "Pigeon");
    }

}
