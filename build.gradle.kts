import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.3.50"
val coroutinesVersion = "1.3.3"
val kotlinLoggingVersion = "1.6.24"
val jsoupVersion = "1.12.1"
val ktorVersion = "1.3.0"
val junitVersion = "5.5.2"
val log4jVersion = "2.11.2"
val log4jApiKotlinVersion = "1.0.0"
val urilibVersion = "1.0.11"


plugins {
    kotlin("jvm") version "1.3.50"
}

group = "com.oxyggen.c4k"
version = "0.1"


repositories {
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/ktor")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("com.oxyggen.net:urilib:$urilibVersion")
    //implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    //implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:$log4jApiKotlinVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}   