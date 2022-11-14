package sampletargetj;

import java.util.List;
import java.util.stream.Collectors;

//@Autowire
public class Bean1 {

    final IService1 service1;
    public Bean1(IService1 service1) {
        this.service1=service1;
    }

    public String greet(List<String> names)  {
        return names.stream().map(it-> service1.sayHello(it) ).collect(Collectors.joining(","));
    }

    public String officialGreet(List<Pair<String, String>> names)  {
        return names.stream().map ( it-> service1.sayHello(it.first, it.second) ).collect(Collectors.joining(","));
    }
}