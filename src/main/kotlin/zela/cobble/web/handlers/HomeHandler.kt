package zela.cobble.web.handlers

import org.http4k.core.*
import zela.cobble.storages.UserStorage
import zela.cobble.templates.ContextAwareViewRender
import zela.cobble.web.models.HomePageVM

class HomeHandler(val renderer: ContextAwareViewRender): HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = HomePageVM()
        val a = 0

        return Response(Status.OK).with(renderer(request) of viewModel)





    }
}