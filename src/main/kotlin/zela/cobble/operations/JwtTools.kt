package zela.cobble.operations

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import java.util.Date

class JwtTools(val secret: String, val issuer: String, val tokenExpiry: Long)
{
    private val algorithm: Algorithm = try {
        Algorithm.HMAC512(secret)
    } catch (e: IllegalArgumentException) {
        throw RuntimeException("JWT secret cannot be null or empty", e)
    }


    fun createToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + tokenExpiry)
        return JWT.create()
            .withSubject(username)
            .withIssuer(issuer)
            .withIssuedAt(now)
            .withExpiresAt(expiryDate)
            .sign(algorithm)
    }


    fun checkJwtToken(token: String): String? {
        val algorithm = Algorithm.HMAC512(secret)
        val verifier: JWTVerifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
        try {
            val decodedJWT: DecodedJWT = verifier.verify(token)
            return decodedJWT.subject
        } catch (_: JWTVerificationException){

        }
        return null
    }
}
