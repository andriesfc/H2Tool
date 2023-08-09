package h2tool

import h2tool.ext.ExtendedToolRunner
import h2tool.utils.props
import h2tool.utils.valueOf
import h2tool.wrapped.WrappedToolRunner
import kotlin.system.exitProcess

object App : List<ToolRunner> by (WrappedToolRunner + ExtendedToolRunner) {

    private val props = props<App>()
    private val description: String get() = props.valueOf("h2.tool.description")
    private val h2Version: String get() = props.valueOf("h2.version")
    private val appVersion: String get() = props.valueOf("h2.app.version")

    private fun exitWithHelp(rc: Int, requested: String? = null): Nothing {
        printHelp(requested)
        exitProcess(rc)
    }

    private fun printHelp(unsupportedCommand: String? = null) {


        println("Welcome to H2 (tools) v.$appVersion (H2 v$h2Version)")
        println()
        println(
            when (unsupportedCommand) {
                null -> description
                else -> "Unsupported Command: $unsupportedCommand"
            }
        )

        val wrapped = filterIsInstance<WrappedToolRunner>().toSortedSet(compareBy(ToolRunner::command))
        val extended = (this - wrapped.toSet()).sortedBy(ToolRunner::command)

        wrapped.takeUnless { it.isEmpty() }?.apply {
            println()
            println("Following wrapped tools are available : ")
            println()
            forEachIndexed { index: Int, toolRunner: ToolRunner ->
                println("(${index + 1}): ${toolRunner.command} - ${toolRunner.shortSummary}")
            }
        }

        extended.takeUnless { it.isEmpty() }?.apply {
            println()
            println("Some other tools (not officially supplied by H2):")
            println()
            forEachIndexed { index: Int, toolRunner: ToolRunner ->
                println("(${index + 1}): ${toolRunner.command} - ${toolRunner.shortSummary}")
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {

        if (args.isEmpty())
            exitWithHelp(0)

        val toolCommand = args.first()
        val toolArgs = args.sliceArray(1..<args.size)
        val tool = find { it.command == toolCommand } ?: exitWithHelp(1, toolCommand)

        tool.run(toolArgs)
    }
}
