fun main() {
    var userEntity = UserEntity(0, "frankie")
    println(userEntity)

    HelloKotlin::class.constructors.map (::println)
}

class HelloKotlin {
    fun hello() {

    }
}