import org.http4k.internal.ModuleLicense.Apache2

description = "Http4k Serverless support for Google Cloud Functions"

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-serverless-core"))
    api(project(":http4k-format-moshi")) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
    }
    api("com.google.cloud.functions:functions-framework-api:_")
    testImplementation(testFixtures(project(":http4k-core")))
}