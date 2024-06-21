package zela.cobble

import org.http4k.core.*
import zela.cobble.formats.JacksonMessage
import zela.cobble.formats.imageFile
import zela.cobble.formats.jacksonMessageLens
import zela.cobble.formats.nameField
import zela.cobble.formats.strictFormBody
import zela.cobble.models.PebbleViewModel
import zela.cobble.rpc.JsonRpcCounter
import zela.cobble.rpc.JsonRpcCounterErrorHandler
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.jsonrpc.JsonRpc
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.template.PebbleTemplates
import org.http4k.template.viewModel
import zela.cobble.templates.ContextAwarePebbleTemplates
import zela.cobble.templates.ContextAwareViewRender

val counter = JsonRpcCounter()
val app: HttpHandler = routes(
    "/ping" bind GET to {
        Response(OK).body("pong")
    },

    "/formats/multipart" bind POST to { request ->
        // to extract the contents, we first extract the form and then extract the fields from it using the lenses
        // NOTE: we are "using" the form body here because we want to close the underlying file streams
        strictFormBody(request).use {
            println(nameField(it))
            println(imageFile(it))
        }
    
        Response(OK)
    },

    "/formats/json/jackson" bind GET to {
        Response(OK).with(jacksonMessageLens of JacksonMessage("Barry", "Hello there!"))
    },

    "/templates/pebble" bind GET to {
        val renderer = PebbleTemplates().CachingClasspath()
        val view = Body.viewModel(renderer, TEXT_HTML).toLens()
        val viewModel = PebbleViewModel("Hello there!")
        Response(OK).with(view of viewModel)
    },

    "/testing/kotest" bind GET to {request ->
        Response(OK).body("Echo '${request.bodyString()}'")
    },

    "/jsonrpc" bind POST to JsonRpc.auto(Jackson, JsonRpcCounterErrorHandler) {
        method("increment", handler(counter::increment))
        method("current", handler(counter::currentValue))
    }
)

fun main() {

    val contexts = RequestContexts()
    val renderer = ContextAwarePebbleTemplates().HotReload("src/main/resources")
    val htmlView = ContextAwareViewRender.withContentType(renderer, ContentType.TEXT_HTML)
    val appWithStaticResources = routes(
        static(ResourceLoader.Classpath("/zela/cobble/public")),
        router(htmlView),
    )

    val server =  appWithStaticResources.asServer(Netty(9000)).start()

    println("Server started on http://localhost:" + server.port())
}
