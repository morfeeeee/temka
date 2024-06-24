package zela.cobble.operations

import zela.cobble.domain.User
import zela.cobble.generators.SaltGenerator
import zela.cobble.storages.SaltStorage
import zela.cobble.storages.UserStorage
import java.security.MessageDigest
import java.time.LocalDateTime


class UserRegistrationOperation(
    val userStorage: UserStorage,
    private val saltStorage: SaltStorage,
    private val saltGenerator: SaltGenerator
) {
    fun registerUser(username: String, password: String, dateTime: LocalDateTime, role: String) {
        val salt = saltGenerator.generateSalt()
        saltStorage.addSalt(username, salt)

        val saltedPassword = password + salt
        val passwordHash = hashPassword(saltedPassword)

        val user = User(username, passwordHash, dateTime,role )
        userStorage.addUser(user)
    }


    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}