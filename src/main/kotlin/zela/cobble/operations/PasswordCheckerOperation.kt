package zela.cobble.operations


import zela.cobble.storages.SaltStorage
import zela.cobble.storages.UserStorage
import java.security.MessageDigest
import java.util.HexFormat

class PasswordCheckerOperation(
    private val userStorage: UserStorage,
    private val saltStorage: SaltStorage
) {
    fun checkPassword(username: String, password: String): Boolean {
        val user = userStorage.findUser(username) ?: return false
        val salt = saltStorage.getSalt(username) ?: return false
        val saltedPassword = password + salt
        val passwordHash = hashPassword(saltedPassword)

        return user.password == passwordHash
    }



    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
