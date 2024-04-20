import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.6"
    id("io.spring.dependency-management") version "1.1.4"
    id("jacoco")
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.jpa") version "1.3.72"
    application
}

group = "org.example.k8s_kafka_consumer"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.mockk:mockk:1.12.0")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("io.fabric8:kubernetes-client:6.9.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.json:json:20231013")
    implementation("com.h2database:h2")
    testImplementation("org.awaitility:awaitility:4.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy (tasks.jacocoTestReport)
}

application {
    mainClass.set("org.example.k8s_kafka_consumer.ApplicationKt")
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            classDirectories.setFrom(classDirectories.files.map {
                fileTree(it).matching {
                    setExcludes(
                        listOf(
                            "org/example/k8s_kafka_consumer/config/**",
                            "org/example/k8s_kafka_consumer/controllers/**",
                            "org/example/k8s_kafka_consumer/utils/K8SApplicationType"
                            )
                        )
                    }
                }
            )
        }
    }
}

tasks.withType<JacocoReport> {
    dependsOn(tasks.test)
    finalizedBy(tasks.jacocoTestCoverageVerification)
    afterEvaluate {
        classDirectories.setFrom(classDirectories.files.map {
            fileTree(it).matching {
                setExcludes(
                    listOf(
                        "org/example/k8s_kafka_consumer/config/**",
                        "org/example/k8s_kafka_consumer/controllers/**",
                        "org/example/k8s_kafka_consumer/utils/K8SApplicationType"
                    )
                )
            }
        })
    }
}