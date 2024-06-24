package zela.cobble.web.handlers

import org.http4k.core.*
import zela.cobble.templates.ContextAwareViewRender
import zela.cobble.web.models.HomePageVM
import zela.cobble.web.models.ServicesVM

class ServicesHandler(val renderer: ContextAwareViewRender): HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = ServicesVM()
        return Response(Status.OK).with(renderer(request) of viewModel)





    }
}