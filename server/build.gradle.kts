plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    // 2. Ktor 플러그인 적용
    alias(libs.plugins.ktor)
}
group = "com.kotlinopenmission8.server"
version = "0.0.1"

val ktor_version = "3.3.1"
val logback_version = "1.5.18"
val kotlin_version = "2.2.20"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    // The new dependency to be added
    implementation("io.ktor:ktor-server-status-pages:${ktor_version}")
    // The existing dependencies
    implementation("io.ktor:ktor-server-core-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-netty-jvm:${ktor_version}")
    implementation("ch.qos.logback:logback-classic:${logback_version}")
    testImplementation("io.ktor:ktor-server-test-host-jvm:${ktor_version}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${kotlin_version}")
}