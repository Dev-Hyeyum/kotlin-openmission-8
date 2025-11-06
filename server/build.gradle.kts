plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    // 2. Ktor 플러그인 적용
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}
group = "com.kotlinopenmission8.server"
version = "0.0.1"

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
    implementation("io.ktor:ktor-server-content-negotiation:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-status-pages:${libs.versions.ktor.get()}")
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    // 테스트 의존성
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}