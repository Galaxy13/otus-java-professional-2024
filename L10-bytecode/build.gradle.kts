import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
    id("application")
}

tasks {
    create<ShadowJar>("autoLoggerJar") {
        archiveBaseName.set("autoLogger")
        archiveVersion.set("")
        archiveClassifier.set("")
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to "com.galaxy13.asm.Main",
                    "Premain-Class" to "com.galaxy13.autologger.Agent"
                )
            )
        }
        from(sourceSets.main.get().output)
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }

    build {
        dependsOn("autoLoggerJar")
    }
}

application {
    mainClass.set("com.galaxy13.asm.Main")

    val agentJar = File(layout.buildDirectory.get().toString(), "libs/autoLogger.jar").absolutePath

    applicationDefaultJvmArgs = listOf("-javaagent:$agentJar")
}

dependencies {
    implementation("org.ow2.asm:asm-commons")
    implementation("org.ow2.asm:asm-util")
    implementation("ch.qos.logback:logback-classic")
}
