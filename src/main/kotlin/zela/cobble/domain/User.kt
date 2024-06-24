package zela.cobble.domain


import java.time.LocalDateTime


class User(
    val nameLog: String,
    val password: String,
    val date: LocalDateTime?,
    val role: String
)




