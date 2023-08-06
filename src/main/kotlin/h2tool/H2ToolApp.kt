package h2tool

import kotlin.system.exitProcess

fun main(args: Array<String>) {

    if (args.isEmpty()) {
        printHelp()
        exitProcess(0)
    }

    val (requested, commandHandler: ((Array<String>) -> Unit)?) = args.first()
        .let { rc -> rc to H2Tool.entries.firstOrNull { it.command == rc }?.action }

    if (commandHandler == null) {
        printHelp(requested)
        exitProcess(0)
    }

    val commandHandlerArgs = args.sliceArray(1..<args.size)
    commandHandler(commandHandlerArgs)

}

private fun printHelp(requested: String? = null) {
    requested?.also {
        println("Unsupported command: $requested")
        println()
    }
    println("The following commands are supported: ")
    println()
    H2Tool.entries.map(H2Tool::command).forEachIndexed { i, s ->
        println("(%d) $s".format(i + 1, s))
    }
    println()
    println("To see help on any of them run: h2 <command> -help")
    println("for example:")
    println()
    println("h2 ${H2Tool.Recover.command} -help")
}

internal enum class H2Tool(val command: String, val action: (Array<String>) -> Unit) {
    Backup("backup", org.h2.tools.Backup::main),
    Console("console", org.h2.tools.Console::main),
    ChangeFileEncryption("change-file-encryption", org.h2.tools.ChangeFileEncryption::main),
    ConvertTraceFile("convert-trace-file", org.h2.tools.ConvertTraceFile::main),
    CreateCluster("create-cluster", org.h2.tools.CreateCluster::main),
    DeleteDbFiles("delete-db-files", org.h2.tools.DeleteDbFiles::main),
    GuiConsole("gui-console", org.h2.tools.GUIConsole::main),
    Recover("recover", org.h2.tools.Recover::main),
    RunScript("run-script", org.h2.tools.RunScript::main),
    Script("script", org.h2.tools.Script::main),
    Server("server", org.h2.tools.Server::main),
    Shell("shell", org.h2.tools.Shell::main)
}

