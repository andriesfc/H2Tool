package h2tool.ext

import h2tool.Tool

sealed class ExtTool(override val command: String, override val description: String) : Tool {

    data object ListRunning : ExtTool("list-running", "List all running severs") {
        override fun run(args: Array<String>) {
        }
    }

    companion object : List<ExtTool> by listOf(
        ListRunning
    )
}