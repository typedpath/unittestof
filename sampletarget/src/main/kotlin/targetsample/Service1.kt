package targetsample

interface IService1 {
    fun sayHello(name: String) : String;
    fun sayHello(name: String, lastName: String) : String;

}

class Service1 : IService1 {
    override fun sayHello(name: String) = "hello Mr $name"
    override fun sayHello(name: String, lastName: String) : String = "hello Mr $name $lastName"

}