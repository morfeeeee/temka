package zela.cobble.web.handlers


import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.findSingle
import org.http4k.core.with
import zela.cobble.config.readConfiguration
import zela.cobble.operations.JwtTools
import zela.cobble.operations.PasswordCheckerOperation
import zela.cobble.templates.ContextAwareViewRender


class LoginHandler(val renderer: ContextAwareViewRender, val userPasswordChecker: PasswordCheckerOperation) : HttpHandler {
    override fun invoke(request: Request): Response {


        val form = request.form()
        var nameLog = form.findSingle("login")
        var password = form.findSingle("password")

        if (nameLog != null) {
            nameLog = nameLog.trim()
        }
        if (password != null) {
            password = password.trim()
        }
        val config = readConfiguration()
        val jwtConfig = config.jwtConfig
        val jwtTools = JwtTools(
            secret = jwtConfig.secret,
            issuer = jwtConfig.organization,
            tokenExpiry = jwtConfig.tokenValidity
        )

//        if (!userPasswordChecker.checkPassword(nameLog!!, password!!)) {
//            return Response(Status.OK).with(renderer(request) of ErrorLoginFormVM(nameLog, password,"Ошибка: Неправильное имя пользователя или пароль."))
//
//        }



        val token = jwtTools.createToken(nameLog!!)

        return Response(Status.FOUND).header("Location",  "/").cookie(Cookie("auth", token))




    }
}