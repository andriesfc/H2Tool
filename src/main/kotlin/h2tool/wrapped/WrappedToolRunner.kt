package h2tool.wrapped

import h2tool.ToolRunner
import h2tool.utils.Paragraph.TrimmedAs.KDoc
import h2tool.utils.p

internal sealed class WrappedToolRunner(
    override val command: String,
    override val shortSummary: String,
    override val summary: String? = null,
    private val runAction: (Array<String>) -> Unit,
) : ToolRunner {
    override var helpOpt: String? = "-help"; protected set
    override fun run(args: Array<String>) = runAction(args)

    data object Backup : WrappedToolRunner(
        command = "backup",
        shortSummary = "Creates a backup of a database",
        summary = """
            * This tool copies all database files. The database must be closed before using
            * this tool. To create a backup while the database is in use, run the BACKUP
            * SQL statement. In an emergency, for example if the application is not
            * responding, creating a backup using the Backup tool is possible by using the
            * quiet mode. However, if the database is changed while the backup is running
            * in quiet mode, the backup could be corrupt.
            """.p(KDoc),
        org.h2.tools.Backup::main
    )

    data object Console : WrappedToolRunner(
        command = "console",
        shortSummary = "Starts the H2 Console (web-) server, as well as the TCP and PG server.",
        runAction = org.h2.tools.Console::main
    ) {
        init {
            helpOpt = null
        }
    }

    data object ChangeFileEncryption : WrappedToolRunner(
        command = "change-file-encryption",
        shortSummary = "Allows changing the database file encryption password or algorithm.",
        summary = """
            * This tool can not be used to change a password of a user.
            * The database must be closed before using this tool. 
            """.p(KDoc),
        runAction = org.h2.tools.ChangeFileEncryption::main
    )

    data object ConvertTraceFile : WrappedToolRunner(
        command = "convert-trace-file",
        shortSummary = """
            Converts a ".trace.db" file to a SQL script and Java source code.
            SQL statement statistics are listed as well.""".p(),
        runAction = org.h2.tools.ConvertTraceFile::main
    )

    data object CreateCluster : WrappedToolRunner(
        command = "create-cluster",
        shortSummary = "Creates a cluster from a stand-alone database. " +
                "Copies a database to another location if required.",
        runAction = org.h2.tools.CreateCluster::main
    )

    data object DeleteDbFiles : WrappedToolRunner(
        command = "delete-db-files",
        shortSummary = "Deletes all files belonging to database. Database must be closed before calling this tool",
        runAction = org.h2.tools.DeleteDbFiles::main
    )

    data object Recover : WrappedToolRunner(
        command = "recover",
        shortSummary = "Helps recover a corrupted database.",
        runAction = org.h2.tools.Recover::main
    )

    data object Script : WrappedToolRunner(
        command = "script",
        shortSummary = "Creates a SQL script file by extracting the schema and data of a database.",
        runAction = org.h2.tools.Script::main
    )

    data object RunScript : WrappedToolRunner(
        command = "run",
        shortSummary = "Runs a SQL script against a database.",
        runAction = org.h2.tools.RunScript::main
    )

    data object Server : WrappedToolRunner(
        command = "serve",
        shortSummary = "Starts the H2 Console (web-) server, TCP, and PG server.",
        runAction = org.h2.tools.Server::main
    )

    data object Shell : WrappedToolRunner(
        command = "shell",
        shortSummary = "Interactive command line tool to access a database using JDBC.",
        runAction = org.h2.tools.Shell::main
    )

    data object Restore : WrappedToolRunner(
        command = "restore",
        shortSummary = "Restores a H2 database by extracting the database files from a .zip file.",
        runAction = org.h2.tools.Restore::main
    )

    data object Profile : WrappedToolRunner(
        command = "profile",
        shortSummary = "A simple CPU profiling tool similar to java -Xrunhprof",
        summary = """
             * A simple CPU profiling tool similar to java -Xrunhprof. It can be used
             * in-process (to profile the current application) or as a standalone program
             * (to profile a different process, or files containing full thread dumps).
            """.p(KDoc),
        runAction = org.h2.util.Profiler::main
    ) {
        init {
            helpOpt = null
        }
    }

    companion object : List<WrappedToolRunner> by listOf(
        Backup,
        Console,
        ChangeFileEncryption,
        ConvertTraceFile,
        CreateCluster,
        DeleteDbFiles,
        Recover,
        Script,
        RunScript,
        Server,
        Shell,
        Restore,
        Profile
    )

}

