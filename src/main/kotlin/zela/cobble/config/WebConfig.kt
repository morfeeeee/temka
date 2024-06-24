package zela.cobble.config


import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.int

data class WebConfig(
    val webPort: Int,
) {
    companion object {
        val webPortLens = EnvironmentKey.int().required("web.port", "Application web port")
        val defaultEnv = Environment.defaults(
            webPortLens of 1515,
        )
        fun from(env: Environment): WebConfig {
            return WebConfig(webPortLens(env))
        }
    }

}