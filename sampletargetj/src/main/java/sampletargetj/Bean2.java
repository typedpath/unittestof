package sampletargetj;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Autowire
public class Bean2 {

    final IService1 service1;
    final IService2 service2;
    final IService3 service3;

    public Bean2(IService1 service1, IService2 service2, IService3 service3) {
        this.service1 = service1;
        this.service2 = service2;
        this.service3 = service3;
    }

    public String greet(List<String> names)  {
        Stream<String> stream1 = names.stream().map(it-> service1.sayHello(it) );
        Stream<String> stream2 = names.stream().map(it-> service2.sayHello2(it) );
        Stream<String> stream3 = names.stream().map(it-> service3.sayHello3(it) );
        return Stream.concat(Stream.concat(stream1, stream2), stream3).collect(Collectors.joining(","));
    }

    public String officialGreet(List<Pair<String, String>> names)  {
        Stream<String> stream1 = names.stream().map(it-> service1.sayHello(it.first, it.second) );
        Stream<String> stream2 = names.stream().map(it-> service2.sayHello2(it.first, it.second) );
        Stream<String> stream3 = names.stream().map(it-> service3.sayHello3(it.first, it.second));
        return Stream.concat(Stream.concat(stream1, stream2), stream3).collect(Collectors.joining(","));
    }
}