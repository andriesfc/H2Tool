package h2tool

import props

internal sealed class H2Tool(
    val command: String,
    private val wrapped: Boolean,
    val action: (Array<String>) -> Unit,
) {

    fun label(): String = buildString {
        append(command)
        if (wrapped) {
            append(" (wrapped)")
        }
    }

    constructor(command: String, action: (Array<String>) -> Unit) : this(command, true, action)

    data object Backup : H2Tool("backup", org.h2.tools.Backup::main)
    data object Console : H2Tool("console", org.h2.tools.Console::main)
    data object ChangeFileEncryption : H2Tool("change-file-encryption", org.h2.tools.ChangeFileEncryption::main)
    data object ConvertTraceFile : H2Tool("convert-trace-file", org.h2.tools.ConvertTraceFile::main)
    data object CreateCluster : H2Tool("create-cluster", org.h2.tools.CreateCluster::main)
    data object DeleteDbFiles : H2Tool("delete-db-files", org.h2.tools.DeleteDbFiles::main)
    data object GuiConsole : H2Tool("gui-console", org.h2.tools.GUIConsole::main)
    data object Recover : H2Tool("recover", org.h2.tools.Recover::main)
    data object RunScript : H2Tool("run-script", org.h2.tools.RunScript::main)
    data object Script : H2Tool("script", org.h2.tools.Script::main)
    data object Server : H2Tool("server", org.h2.tools.Server::main)
    data object Shell : H2Tool("shell", org.h2.tools.Shell::main)
    
    companion object: List<H2Tool> by listOf(
        Backup,
        Console,
        ChangeFileEncryption,
        ConvertTraceFile,
        CreateCluster,
        DeleteDbFiles,
        GuiConsole,
        Recover,
        RunScript,
        Script,
        Server,
        Shell
    ) {
        private val props = props<H2Tool>()
        fun prop(property: String): String? = props[property]
        fun of(command: String): H2Tool? = H2Tool.firstOrNull { tool -> tool.command == command }
    }
}

