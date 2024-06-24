package zela.cobble.web.filters

import zela.cobble.domain.User
import zela.cobble.operations.JwtTools
import zela.cobble.storages.UserStorage


import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.cookie.Cookie
import org.http4k.lens.BiDiLens
import org.http4k.lens.RequestContextLens


class AuthFilter(
    private val jwtTools: JwtTools,
    private val userLens: RequestContextLens<User?>,
    private val cookieLens: BiDiLens<Request, Cookie?>,
    private val userStorage: UserStorage,

    ) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler {
        return { request: Request ->
            val authCookie = cookieLens(request)
            if (authCookie != null) {
                val token = authCookie.value
                val username = jwtTools.checkJwtToken(token)
                if (username != null) {
                    val user = userStorage.findUser(username)
                    if (user != null) {
                        userLens(user, request)


                    }
                }
            }
            next(request)
        }
    }
}
