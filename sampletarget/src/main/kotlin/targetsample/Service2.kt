package targetsample

interface IService2 {
    fun log(text: String) : String
    fun log(severity: String, text: String) : String
}

class Service2 : IService2 {
    override fun log(text: String) : String
    {
      log ("info", text)
      return "logged"
    }
    override fun log(severity: String, text: String) : String  {
        println("$severity : $text")
        return "logged"
    }
}