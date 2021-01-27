plugins {
    kotlin
    `kotlin-kapt`
    detekt
    document
}

dependencies {
    implementation(project(":core"))
    implementation("com.squareup:kotlinpoet:1.7.2")
    implementation("com.google.auto.service:auto-service:1.0-rc6")
    kapt("com.google.auto.service:auto-service:1.0-rc6")
}