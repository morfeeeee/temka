package zela.cobble.operations

import zela.cobble.domain.User
import zela.cobble.generators.SaltGenerator
import zela.cobble.storages.SaltStorage
import zela.cobble.storages.UserStorage
import java.security.MessageDigest
import java.sql.Connection
import java.time.LocalDateTime


class UserRegistrationOperation(
    val userStorage: UserStorage,
    private val saltStorage: SaltStorage,
    private val saltGenerator: SaltGenerator,
    private val connection: Connection
) {
    fun registerUser(username: String, password: String, dateTime: LocalDateTime, role: String) {
        val salt = saltGenerator.generateSalt()
        saltStorage.addSalt(username, salt)

        val insertQuery1 = """
                INSERT INTO Salts (username, salt) 
                VALUES (?, ?)
            """
        connection.prepareStatement(insertQuery1).use { stmt ->
            stmt.setString(1, username)
            stmt.setString(2, salt)

            stmt.executeUpdate()
        }

        val saltedPassword = password + salt
        val passwordHash = hashPassword(saltedPassword)

        val user = User(username, passwordHash, dateTime,role )
        val insertQuery = """
                INSERT INTO Users (nameLog, password, date, role) 
                VALUES (?, ?, ?, ?)
            """
        connection.prepareStatement(insertQuery).use { stmt ->
            stmt.setString(1, username)
            stmt.setString(2, passwordHash)
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(dateTime))
            stmt.setString(4, "citizen")
            stmt.executeUpdate()
        }
        userStorage.addUser(user)
    }


    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}