package us.berkovitz.plexapi.myplex

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlValue

typealias PlexBool = @Serializable(PlexBoolSerializer::class) Boolean

object PlexBoolSerializer : KSerializer<Boolean> {
    override fun deserialize(decoder: Decoder): Boolean {
        val strVal = decoder.decodeString()
        val intVal = strVal.toIntOrNull()
        return intVal == 1
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeString(if (value) "1" else "0")
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("us.berkovitz.plexapi.myplex.PlexBool", PrimitiveKind.STRING)
}

@Serializable
@SerialName("error")
data class Error(
    @XmlValue(true) val message: String
)

@Serializable
@SerialName("errors")
data class Errors(
    @XmlElement(true) val errors: List<Error>
)

suspend fun handleErrors(res: HttpResponse) {
    if (!res.status.isSuccess()) {
        try {
            val body: Errors = res.body()
            if (res.status.value == HttpStatusCode.Unauthorized.value) {
                throw AuthorizationException(body.errors.joinToString(";"), null)
            } else {
                throw MyPlexException(body.errors.joinToString(";"), null)
            }
        } catch (exc: Exception) {
            if (res.status.value == HttpStatusCode.Unauthorized.value) {
                throw AuthorizationException("unauthorized", null)
            } else if (res.status.value == HttpStatusCode.NotFound.value) {
                throw NotFoundException("resource not found", null)
            }
        }
    }
}

class AuthorizationException(msg: String, cause: Throwable?) : Exception(msg, cause)
class DecodeException(msg: String, cause: Throwable?) : Exception(msg, cause)
class MyPlexException(msg: String, cause: Throwable?) : Exception(msg, cause)
class NotFoundException(msg: String, cause: Throwable?) : Exception(msg, cause)
