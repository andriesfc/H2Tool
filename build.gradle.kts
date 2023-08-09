@file:Suppress("PropertyName")

import java.io.IOException
import java.time.LocalDateTime
import java.util.*

plugins {
    id("common-conventions")
    application
}

val h2_version = "2.2.220"

version = "0.0.1"

description =
    "This single tool exposes the builtin H2 Command line commands, " +
            "as well as some extended tools directly implemented on the H2 database."

dependencies {
    implementation("com.h2database:h2:$h2_version")
}

application {
    mainClass.set("h2tool.App")
    applicationName = "h2"
    description = project.description
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
        val buildTime = LocalDateTime.now().toString()
        file("src/main/resources/h2tool/App.properties").also { propsFile ->
            if (false == propsFile.takeUnless(File::exists)?.createNewFile())
                throw GradleException("Failed to create propsFile: $propsFile")
            val properties = Properties().apply { propsFile.reader().use(::load) }
            val updated = properties.run {
                updated("h2.version", h2_version)
                        || updated("h2.app.version", project.version.toString())
                        || updated("h2.tool.description", project.description.toString())
            }
            if (updated) propsFile.writer().use { writer ->
                properties.store(
                    writer,
                    "Updated by the build on $buildTime"
                )
            }
        }
    }
}

