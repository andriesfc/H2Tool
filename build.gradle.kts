@file:Suppress("PropertyName")

plugins {
    id("common-conventions")
    application
}

val h2_version = "2.2.220"

dependencies {
    implementation("com.h2database:h2:$h2_version")
}

application {
    mainClass.set("h2tool.H2ToolAppKt")
    applicationName = "h2"
    description = "H2 Tools & convenience wrapper for H2"
}