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
import java.sql.DriverManager
import kotlin.concurrent.thread



fun main() {

    val listUsers = mutableListOf<User>()
    val mapSalt = mutableMapOf<String, String>()

    val url = "jdbc:mysql://localhost:3306/database"
    val connect = DriverManager.getConnection(url, "root", "Nagano106")

    val query = "SELECT nameLog, date, password, role FROM Users"
    connect.createStatement().use { stmt ->
        val resultSet = stmt.executeQuery(query)
        while (resultSet.next()) {
            val name = resultSet.getString("nameLog")
            val date = resultSet.getTimestamp("date").toLocalDateTime()
            val password = resultSet.getString("password")
            val role = resultSet.getString("role")
            val user = User(name,password,date,role)
            listUsers.add(user)

        }
    }


    val querySalt = "SELECT username, salt FROM Salts"
    connect.createStatement().use { stmt ->
        val resultSet = stmt.executeQuery(querySalt)
        while (resultSet.next()) {
            val name = resultSet.getString("username")
            val salt = resultSet.getString("salt")
            mapSalt[name] = salt

        }
    }

    for(i in listUsers){
        println("Login: ${i.nameLog}  Password: ${i.password}  Role: ${i.role}  Date: ${i.date}" )
    }

    val saltGenerator = SaltGenerator()
    val saltStorage = SaltStorage(mapSalt)
    val userStorage = UserStorage(listUsers)
    val userRegistration = UserRegistrationOperation(userStorage, saltStorage, saltGenerator,connect)
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
        router(htmlView, userRegistration,userPasswordChecker,),
    )

    val server =  ServerFilters.InitialiseRequestContext(contexts).then(authFilter).then(appWithStaticResources).asServer(Netty(9000)).start()
    println("Server started on http://localhost:" + server.port())


//    val dropTableQuery = "DROP TABLE IF EXISTS Salts"
//    val drop = "DROP TABLE IF EXISTS Users"
//    val createTableQuery = """
//        CREATE TABLE Users (
//            id MEDIUMINT NOT NULL AUTO_INCREMENT,
//            nameLog CHAR(30) NOT NULL,
//            password CHAR(150) NOT NULL,
//            date DATETIME NOT NULL,
//            role CHAR(30) NOT NULL,
//            PRIMARY KEY (id)
//        )
//    """
//    val createTableQuery1 = """
//        CREATE TABLE Salts (
//            id MEDIUMINT NOT NULL AUTO_INCREMENT,
//            username CHAR(30) NOT NULL,
//            salt CHAR(150) NOT NULL,
//            PRIMARY KEY (id)
//        )
//    """
//    connect.createStatement().use { stmt ->
//        stmt.executeUpdate(dropTableQuery)
//        stmt.executeUpdate(createTableQuery)
//        stmt.executeUpdate(createTableQuery1)
//        stmt.executeUpdate(drop)
//    }

    println("you are connected")


    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop()
        connect.close()
    })

    while (true) {
        Thread.sleep(1000)
    }


}
