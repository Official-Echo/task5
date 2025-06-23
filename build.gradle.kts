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
	testImplementation("junit:junit:4.13.2")
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