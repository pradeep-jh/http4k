import org.http4k.internal.ModuleLicense.Http4kCommunity

description = "Deprecated. Use : :http4k-connect-ai-openai-fake"

val license by project.extra { Http4kCommunity }

plugins {
    id("org.http4k.community")
    id("org.http4k.connect.module")
    id("org.http4k.connect.fake")
}

dependencies {
    api(project(":http4k-connect-ai-openai-fake"))
}