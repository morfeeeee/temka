package zela.cobble.web.handlers

import org.http4k.core.*
import zela.cobble.templates.ContextAwareViewRender
import zela.cobble.web.models.ContactsVM
import zela.cobble.web.models.HomePageVM

class ContactsHandler(val renderer: ContextAwareViewRender): HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = ContactsVM()
        return Response(Status.OK).with(renderer(request) of viewModel)

    }
}