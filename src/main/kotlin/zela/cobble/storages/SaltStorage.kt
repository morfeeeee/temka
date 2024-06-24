package zela.cobble.storages


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

class SaltStorage(private val salts: MutableMap<String,String>) {

    fun addSalt(username: String, salt: String) {
        salts[username] = salt
    }

    fun getSalt(username: String): String? {
        return salts[username]
    }
    fun saveToFile(filePath: String) {
        val objectMapper = jacksonObjectMapper()
        val saltString = objectMapper.writeValueAsString(salts)
        val file = File(filePath)
        file.writeText(saltString, Charsets.UTF_8)
    }
}