import org.http4k.internal.ModuleLicense.Http4kCommunity

description = "http4k KondorJson support"

val license by project.extra { Http4kCommunity }

plugins {
    id("org.http4k.community")
}

dependencies {
    api(project(":http4k-format-core"))
    api("com.ubertob.kondor:kondor-core:_")
    testImplementation(project(":http4k-core"))
    testImplementation(project(":http4k-api-openapi"))
    testImplementation(project(":http4k-jsonrpc"))
    testImplementation("dev.forkhandles:values4k:_")
    testImplementation(testFixtures(project(":http4k-core")))
    testImplementation(testFixtures(project(":http4k-format-core")))
    testImplementation(testFixtures(project(":http4k-contract")))
    testImplementation(testFixtures(project(":http4k-jsonrpc")))
}
