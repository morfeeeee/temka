package zela.cobble

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.lens.BiDiLens
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import zela.cobble.templates.ContextAwareViewRender
import zela.cobble.web.handlers.HomeHandler
import zela.cobble.web.handlers.ServicesHandler

fun router(cashType: ContextAwareViewRender): RoutingHttpHandler{

    return routes(
        "/" bind Method.GET to HomeHandler(cashType),
        "/services" bind Method.GET to ServicesHandler(cashType),
//        "/logout" bind Method.GET to LogoutHandler(),
//        "/login" bind Method.POST to LoginHandler(cashType,userPasswordChecker),
//        "/login" bind Method.GET to LoginGetHandler(cashType),
//        "/users" bind Method.GET to UsersHandler(cashType, permissionLens, userStorage),
//        "/users/{username}/delete" bind Method.POST to UserDeleteHandler(cashType, userStorage),
//        "/users/{username}/delete" bind Method.GET to UserGetDeleteHandler(cashType, userStorage, ),
//        "/users/{username}/edit" bind Method.POST to UserEditHandler(cashType, userStorage),
//        "/users/{username}/edit" bind Method.GET to UserGetEditandler(cashType,userStorage),
//        "/users/add" bind Method.POST to UserAddHandler(cashType,userRegistr),
//        "/users/add" bind Method.GET to UserGetAddHandler(cashType, permissionLens),

    )
}
