dependencies {
    implementation ("ch.qos.logback:logback-classic")

    testImplementation("org.assertj:assertj-core")
}

tasks.withType(Test::class) {
    enabled = false
}

tasks.register<JavaExec>("testCustom") {
    val testDir = file("src/test/java")
    val testClasses = mutableListOf<String>()

    mainClass.set("com.galaxy13.Main")
    classpath = sourceSets["test"].runtimeClasspath

    doFirst {
        testDir.walkTopDown().forEach { file ->
            if (file.isFile && file.extension == "java") {
                logger.debug("Found file!")
                val relativePath = file.relativeTo(testDir).path
                val className = relativePath.replace("/", ".").removeSuffix(".java")
                testClasses.add(className)
            }
        }
        if (testClasses.isEmpty()) {
            logger.lifecycle("No test classes found.")
            throw GradleException("Test execution failed: No test classes found.")
        }
        args(testClasses)
    }

    doLast {
        logger.lifecycle("\nTests executed: ${testClasses.joinToString(", ")}")
    }
}

tasks {
    build {
        dependsOn("testCustom")
    }
}
