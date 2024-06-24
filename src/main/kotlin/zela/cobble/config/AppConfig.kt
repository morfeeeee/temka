package zela.cobble.config


import org.http4k.cloudnative.env.Environment
import zela.cobble.config.JwtConfig.Companion.readJwtConfig
import zela.cobble.config.WebConfig.Companion.defaultEnv
import zela.cobble.config.WebConfig.Companion.from


data class AppConfig(
    val webConfig: WebConfig,
    val jwtConfig: JwtConfig
)

val appEnv = Environment.fromResource("zela/cobble/config/app.properties") overrides
        Environment.JVM_PROPERTIES overrides
        Environment.ENV overrides
        defaultEnv



fun readConfiguration(): AppConfig{
    val webConfig = from(appEnv)
    return AppConfig(webConfig, readJwtConfig(appEnv))
}