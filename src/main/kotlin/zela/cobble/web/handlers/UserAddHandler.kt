package zela.cobble.web.handlers

import zela.cobble.operations.UserRegistrationOperation
import zela.cobble.templates.ContextAwareViewRender


import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form
import org.http4k.core.findSingle
import org.http4k.core.with
import zela.cobble.errors.UserFormErrors
import zela.cobble.web.models.ErrorUserFormVM
import java.sql.Connection

import java.time.LocalDateTime


class UserAddHandler(val renderer: ContextAwareViewRender, private val registr: UserRegistrationOperation,) : HttpHandler {
    override fun invoke(request: Request): Response {


        val form = request.form()
        var nameLog = form.findSingle("login")
        var password = form.findSingle("password")
        var confirmPassword = form.findSingle("password1")
//        val role = form.findSingle("role")
        if (nameLog != null) {
            nameLog = nameLog.trim()
        }
        if (password != null) {
            password = password.trim()
        }
        if(confirmPassword != null) {
            confirmPassword = confirmPassword.trim()
        }
        val currentDateTime = LocalDateTime.now()


        val formErrors = UserFormErrors(nameLog,password,confirmPassword, registr.userStorage)
        val errors = formErrors.getFormErrors()
        if(errors.isError()){
            return Response(Status.OK).with(renderer(request) of ErrorUserFormVM(nameLog!!, password!!, confirmPassword!!, errors.getAllErrors()))
        }
        if (nameLog != null && password != null && password == confirmPassword) {
            registr.registerUser(nameLog, password, currentDateTime, "citizen")

            return Response(Status.FOUND).header("Location", "/")
        } else {
            return Response(Status.BAD_REQUEST).body("Invalid registration details")
        }







    }
}