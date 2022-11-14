package targetsample

class Bean1(val service1: IService1) {
    fun greet(names: List<String>) : String {
        return names.map { service1.sayHello(it) }.joinToString(",")
    }
    fun officialGreet(names: List<Pair<String, String>>) : String {
        return names.map { service1.sayHello(it.first, it.second) }.joinToString(",")
    }
}