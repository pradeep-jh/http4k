import org.http4k.internal.ModuleLicense.Apache2

description = "Http4k support for Approval Testing"

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-core"))
    api("com.github.javadev:underscore:_")
    api("org.jsoup:jsoup:_")

    implementation("org.junit.jupiter:junit-jupiter-api:_")
    implementation("com.natpryce:hamkrest:_")
    implementation(project(":http4k-format-jackson-yaml"))
    implementation(project(":http4k-cloudevents"))

    testImplementation(testFixtures(project(":http4k-core")))
    testImplementation(project(":http4k-core"))
    testImplementation(project(":http4k-testing-hamkrest"))
}