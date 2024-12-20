rootProject.name = "otus-java-2024-pro"
include("L01-gradle")
include("L04-generics")
include("L06-annotations")
include("L08-gc:homework")
include("L10-bytecode")
include("L12-solid")
include("L15-patterns:homework")
include("L16-io:homework")
include("L18-jdbc:demo")
include("L18-jdbc:homework")
include("L21-jpql:homework")
include("L22-cache:cache")
include("L22-cache:orm")
include("L25-di:homework")
include("L24-webServer")
include("L28-springDataJdbc")
include("L31-executors")
include("L34-multiprocess")
include("L33-concurrentCollections:QueueDemo")
include("L38-webflux-chat:client-service")
include("L38-webflux-chat:datastore-service")

pluginManagement {
    val jgitver: String by settings
    val dependencyManagement: String by settings
    val springframeworkBoot: String by settings
    val johnrengelmanShadow: String by settings
    val jib: String by settings
    val protobufVer: String by settings
    val sonarlint: String by settings
    val spotless: String by settings
    val jmhPlugin: String by settings

    plugins {
        id("fr.brouillard.oss.gradle.jgitver") version jgitver
        id("io.spring.dependency-management") version dependencyManagement
        id("org.springframework.boot") version springframeworkBoot
        id("com.github.johnrengelman.shadow") version johnrengelmanShadow
        id("com.google.cloud.tools.jib") version jib
        id("com.google.protobuf") version protobufVer
        id("name.remal.sonarlint") version sonarlint
        id("com.diffplug.spotless") version spotless
        id("me.champeau.jmh") version jmhPlugin
    }
}
