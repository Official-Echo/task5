plugins {
	java
	application
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("info.picocli:picocli:4.7.7")
	implementation("net.sf.cssbox:jstyleparser:4.0.1")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
	mainClass.set("task5.environment.App")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "task5.environment.App"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}