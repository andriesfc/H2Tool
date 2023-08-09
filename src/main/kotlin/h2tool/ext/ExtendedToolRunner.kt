package h2tool.ext

import h2tool.ToolRunner

sealed interface ExtendedToolRunner : ToolRunner {



    companion object : List<ExtendedToolRunner> by listOf()
}