package zela.cobble.generators


import java.security.SecureRandom
import kotlin.random.asKotlinRandom

class SaltGenerator(private val length: Int = 100) {
    private val random = SecureRandom().asKotlinRandom()

    fun generateSalt(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random(random) }
            .joinToString("")
    }
}