package zela.cobble.config


import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.long
import org.http4k.lens.secret

data class JwtConfig(
    val secret: String,
    val organization: String,
    val tokenValidity: Long
) {
    companion object {
        val secretLens = EnvironmentKey.secret().required("jwt.secret", "JWT secret")
        val organizationLens = EnvironmentKey.required("jwt.organization", "JWT organization")
        val tokenValidityLens = EnvironmentKey.long().required("jwt.tokenValidity", "JWT token validity in ms")
        fun readJwtConfig(env: Environment): JwtConfig {
            return JwtConfig(
                secret = secretLens(env).toString(),
                organization = organizationLens(env),
                tokenValidity = tokenValidityLens(env)
            )
        }
    }
}

