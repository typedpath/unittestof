package targetsample;

//import com.typedpath.injectwithtest.annotations.Autowire;

//@Autowire
class Bean2(val service1: IService1, val service2: IService2) {
    fun dostuff(str: String) : String{
        service2.log("critical", "doing stuff")
        return service1.sayHello(str)
    }

}
