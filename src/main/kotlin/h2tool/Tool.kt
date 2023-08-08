package h2tool

interface Tool {
    val command: String
    val description: String
    fun run(args: Array<String>)
}