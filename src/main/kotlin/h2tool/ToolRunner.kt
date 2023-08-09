package h2tool

interface ToolRunner {

    val command: String

    val shortSummary: String

    val summary: String?
    val helpOpt: String? get() = null
    fun run(args: Array<String>)

}