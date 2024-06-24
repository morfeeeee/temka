package zela.cobble.web.handlers


import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import zela.cobble.templates.ContextAwareViewRender
import zela.cobble.web.models.AddUserVM


class UserGetAddHandler(val renderer: ContextAwareViewRender,) : HttpHandler {

    override fun invoke(request: Request): Response {
        val roles = mutableListOf<String>()
        roles.add("admin")
        roles.add("cityAdmin")
        roles.add("citizen")
        val viewModel = AddUserVM(roles)
        return Response(Status.OK).with(renderer(request) of viewModel)
    }
}