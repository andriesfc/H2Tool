@file:Suppress("PropertyName")

import java.io.IOException
import java.util.*

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

val distributionTaskGroup = "distribution"
val localInstallDir: Provider<File> = providers.gradleProperty("local.install.dir").map(::File)

fun File.dir(name: String): File = File(this, name).also { dir ->

    if (!dir.exists()) {
        if (!dir.mkdirs())
            throw IOException("Unable to create directory: $dir")
    }

    if (dir.isFile)
        throw IOException("Expected regular directory here: $dir")
}

val Provider<File>.file: File
    get() = orNull
        ?: throw GradleException(
            "No property specified for [local.install.dir]. Set via gradle properties " +
                    "for local user, as pass it via the command line"
        )

fun File.emptied(): File = apply {
    if (exists()) {
        if (!isDirectory) throw IOException("Expected regular directory: $path")
        listFiles()?.forEach { child ->
            when {
                child.isFile -> child.delete()
                child.isDirectory -> child.deleteRecursively()
            }
        }
    }
}

fun File.localized(): File {
    return when {
        path.startsWith("~/") -> File(System.getProperty("user.home"), path.substring(2))
        else -> this
    }
}


val installLocalDist: Task by tasks.creating {
    description = "Installs into a specified local dist folder."
    group = distributionTaskGroup
    dependsOn("installDist")
    doLast {
        val installDir = localInstallDir.file.localized().dir(project.name.lowercase())
        println(installDir)
        copy {
            from(layout.buildDirectory.dir("install/h2"))
            into(installDir.emptied())
            include("**/*")
            duplicatesStrategy = DuplicatesStrategy.FAIL
        }
    }
}

tasks.named("compileKotlin").configure { dependsOn(updateToolProps) }

fun Properties.updated(key: String, update: String): Boolean {
    val current = getProperty(key)
    val updatable = current != update
    takeIf { updatable }?.setProperty(key, update)
    return updatable
}

val updateToolProps: Task by tasks.creating {
    description = "Updates the H2ToolApp.properties"
    group = distributionTaskGroup
    doLast {
        file("src/main/resources/h2tool/H2Tool.properties").also { propsFile ->
            if (false == propsFile.takeUnless(File::exists)?.createNewFile())
                throw GradleException("Failed to create propsFile: $propsFile")
            val properties = Properties().apply { propsFile.reader().use(::load) }
            val updated = properties.updated("h2.version", h2_version)
            if (updated) propsFile.writer().use { writer -> properties.store(writer, "Updated tool props") }
        }
    }
}

