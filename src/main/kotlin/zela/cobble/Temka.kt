package zela.cobble

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
import zela.cobble.domain.User
import zela.cobble.generators.SaltGenerator
import zela.cobble.operations.PasswordCheckerOperation
import zela.cobble.operations.UserRegistrationOperation
import zela.cobble.storages.SaltStorage
import zela.cobble.storages.UserStorage
import zela.cobble.templates.ContextAwarePebbleTemplates
import zela.cobble.templates.ContextAwareViewRender
import java.io.File
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.http4k.core.cookie.Cookie
import org.http4k.lens.BiDiLens
import org.http4k.lens.Cookies
import org.http4k.lens.RequestContextKey
import zela.cobble.config.readConfiguration
import zela.cobble.operations.JwtTools
import zela.cobble.web.filters.AuthFilter
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

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
        val viewModel = PebbleViewModel()
        Response(OK).with(view of viewModel)
    },

    "/testing/kotest" bind GET to {request ->
        Response(OK).body("Echo '${request.bodyString()}'")
    },

    "/jsonrpc" bind POST to JsonRpc.auto(Jackson, JsonRpcCounterErrorHandler) {
        method("increment", handler(counter::increment))
        method("current", handler(counter::currentValue))
    },

    "/main" bind GET to {
        val renderer = PebbleTemplates().CachingClasspath()
        val view = Body.viewModel(renderer, TEXT_HTML).toLens()
        val viewModel = PebbleViewModel()
        Response(OK).with(view of viewModel)
    }
)

fun main() {
    val objectMapper = jacksonObjectMapper()
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
    objectMapper.registerModule(JavaTimeModule())
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    if (!File("Users.json").exists()) {
        File("Salt.json").createNewFile()
        File("Users.json").createNewFile()
        val users = mutableListOf<User>()
        val salt =  mutableMapOf<String, String>()
        val userString = objectMapper.writeValueAsString(users)
        val saltString = objectMapper.writeValueAsString(salt)
        File("Users.json").writeText(userString, Charsets.UTF_8)
        File("Salt.json").writeText(saltString, Charsets.UTF_8)

    }
    val listUsers = mutableListOf<User>()
//    MutableList<User> = objectMapper.readValue(File("Users.json").readText())
    val mapSalt = mutableMapOf<String, String>()
//            MutableMap<String, String> = objectMapper.readValue(File("Salt.json").readText())

    val saltGenerator = SaltGenerator()
    val saltStorage = SaltStorage(mapSalt)
    val userStorage = UserStorage(listUsers)
    val userRegistration = UserRegistrationOperation(userStorage, saltStorage, saltGenerator)
    val userPasswordChecker = PasswordCheckerOperation(userStorage, saltStorage)
    val config = readConfiguration()
    val jwtConfig = config.jwtConfig
    val jwtTools = JwtTools(
        jwtConfig.secret,
        jwtConfig.organization,
        jwtConfig.tokenValidity
    )
    val contexts = RequestContexts()
    val userLens = RequestContextKey.optional<User>(contexts, "user")
    val cookieLens: BiDiLens<Request, Cookie?> = Cookies.optional("auth")

    val renderer = ContextAwarePebbleTemplates().HotReload("src/main/resources")
    val htmlView = ContextAwareViewRender.withContentType(renderer, ContentType.TEXT_HTML)
        .associateContextLens("user", userLens)

    val authFilter = AuthFilter(jwtTools, userLens, cookieLens,userStorage)

    val appWithStaticResources = routes(
        static(ResourceLoader.Classpath("/zela/cobble/public")),
        router(htmlView, userRegistration,userPasswordChecker),
    )

    val server =  ServerFilters.InitialiseRequestContext(contexts).then(authFilter).then(appWithStaticResources).asServer(Netty(9000)).start()

    println("Server started on http://localhost:" + server.port())

    val url = "jdbc:mysql://localhost:3306/data"
    val connect = DriverManager.getConnection(url, "root", "!admin")

    println("you are connected")
    val statement = connect.createStatement()
    statement.executeUpdate("CREATE TABLE Users (id MEDIUMINT NOT NULL AUTO_INCREMENT, name CHAR(30) NOT NULL, PRIMARY KEY (id))")
    statement.executeUpdate("insert into Users (name) values('serega')")
    }