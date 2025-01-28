import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependencyManagement) apply false

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.detekt)
}

group = "io.kskim"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

apply {
    plugin("idea")
    plugin("kotlin")
    plugin("kotlin-spring")
    plugin(libs.plugins.spring.boot.get().pluginId)
    plugin(libs.plugins.spring.dependencyManagement.get().pluginId)
    plugin(libs.plugins.detekt.get().pluginId)
}

dependencies {
    implementation(libs.jvm)
    implementation(libs.reflect)

    implementation(libs.spring.boot.web)
    implementation(libs.jackson)

    implementation(libs.aws.sdk.bom)
    implementation(libs.aws.sdk.dynamodb)
    implementation(libs.aws.sdk.encryption)

    annotationProcessor(libs.spring.boot.configProcessor)

    detektPlugins(libs.detekt.formatting)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

detekt {
    config.from(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = true
}
