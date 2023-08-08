package h2tool

import kotlin.system.exitProcess

fun main(args: Array<String>) {

    if (args.isEmpty())
        exitWithHelp(0)

    val toolCommand = args.first()
    val tool = H2Tool.of(toolCommand)?.action ?: exitWithHelp(1, toolCommand)
    val toolArgs = args.sliceArray(1..<args.size)

    tool(toolArgs)
}

private fun exitWithHelp(rc: Int, requested: String? = null): Nothing {
    printHelp(requested)
    exitProcess(rc)
}

private fun printHelp(requested: String? = null) {

    requested?.also {
        println("Unsupported command: $requested")
        println()
    }

    val h2Version = H2Tool.prop("h2.version")

    print("The following commands are supported: ")
    h2Version?.also { print(" [H2 v.%s]".format(h2Version)) }

    println()

    H2Tool.forEachIndexed { i, tool ->
        println("(%d) %s.".format(i + 1, tool.label()))
    }



    println()
    println("To see help on any of them run: h2 <command> -help")
    println("for example:")
    println()
    println("h2 ${H2Tool.Recover.command} -help")
}


