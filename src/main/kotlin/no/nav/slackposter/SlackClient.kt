package no.nav.slackposter

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.io.path.Path

enum class Severity(val emoji: String) {
    ERROR(":scream:"),
    WARN(":thinking_face"),
    INFO(":information_source:")
}

class SlackClient(
    private val webhookUrl: String,
    private val channel: String,
    private val application: String,
    private val clusterName: String
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
        private val objectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    fun postMessage(text: String, severity: Severity = Severity.INFO) {
        webhookUrl.post(
            objectMapper.writeValueAsString(
                mutableMapOf<String, Any>(
                    "channel" to channel,
                    "username" to "$application ($clusterName)",
                    "text" to text,
                    "icon_emoji" to severity.emoji
                )
            )
        )
    }

    private fun String.post(jsonPayload: String) {
        var connection: HttpURLConnection? = null
        try {
            connection = (Path(this).toUri().toURL().openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 5000
                readTimeout = 5000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")

                outputStream.use { it.bufferedWriter(Charsets.UTF_8).apply { write(jsonPayload); flush() } }
            }

            val responseCode = connection.responseCode

            if (connection.responseCode !in 200..299) {
                logger.warn("response from slack: code=$responseCode")
                return
            }
        } catch (err: SocketTimeoutException) {
            logger.warn("timeout waiting for reply", err)
        } catch (err: IOException) {
            logger.error("error posting to slack: {}", err.message, err)
        } finally {
            connection?.disconnect()
        }
    }
}
