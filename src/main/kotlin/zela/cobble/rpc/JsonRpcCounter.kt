package zela.cobble.rpc

import org.http4k.format.Json
import org.http4k.jsonrpc.ErrorHandler
import org.http4k.jsonrpc.ErrorMessage
import java.util.concurrent.atomic.AtomicInteger

class JsonRpcCounter {
    private val value = AtomicInteger()

    fun increment(amount: Increment): Int = when {
        amount.value == 10 -> throw RuntimeException("Boom!")
        amount.value < 0 -> throw NegativeIncrementException()
        else -> value.addAndGet(amount.value)
    }

    fun currentValue(): Int = value.get()

    data class Increment(val value: Int)

    class NegativeIncrementException : RuntimeException("negative increment not allowed")
}

object JsonRpcCounterErrorHandler : ErrorHandler {
    override fun invoke(error: Throwable): ErrorMessage? = when (error) {
        is JsonRpcCounter.NegativeIncrementException -> NegativeIncrementExceptionMessage()
        else -> null
    }

    private class NegativeIncrementExceptionMessage : ErrorMessage(1, "Increment by negative") {
        override fun <NODE> data(json: Json<NODE>) = json.string("cannot increment counter by negative")
    }
}
