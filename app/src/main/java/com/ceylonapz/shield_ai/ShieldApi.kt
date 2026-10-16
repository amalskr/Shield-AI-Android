package com.ceylonapz.shield_ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

enum class Verdict { SAFE, SUSPICIOUS, SCAM }

data class UrlCheck(val url: String, val malicious: Boolean, val threats: List<String>)

data class Analysis(
    val verdict: Verdict,
    val riskScore: Int,
    val scamType: String,
    val detectedLanguage: String,
    val redFlags: List<String>,
    val explanation: String,
    val actions: List<String>,
    val urlChecks: List<UrlCheck>,
    val latencyMs: Long,
)

class ShieldApiException(message: String) : Exception(message)

/** Same Cloud Run backend as the web app: Web Risk + Gemini on Vertex AI. */
object ShieldApi {
    const val BASE_URL = "https://shield-ai-api-454732198588.asia-southeast1.run.app"

    suspend fun analyze(text: String, replyLanguage: String): Analysis = withContext(Dispatchers.IO) {
        val conn = (URL("$BASE_URL/analyze").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 30_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }
        try {
            val body = JSONObject()
                .put("text", text)
                .put("reply_language", replyLanguage)
                .toString()
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val raw = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (code !in 200..299) throw ShieldApiException("HTTP $code")
            parse(JSONObject(raw))
        } finally {
            conn.disconnect()
        }
    }

    private fun JSONArray?.strings(): List<String> =
        if (this == null) emptyList() else List(length()) { getString(it) }

    private fun parse(j: JSONObject): Analysis {
        val links = j.optJSONArray("url_checks")
        return Analysis(
            verdict = runCatching { Verdict.valueOf(j.getString("verdict")) }.getOrDefault(Verdict.SUSPICIOUS),
            riskScore = j.optInt("risk_score", 50).coerceIn(0, 100),
            scamType = j.optString("scam_type"),
            detectedLanguage = j.optString("detected_language"),
            redFlags = j.optJSONArray("red_flags").strings(),
            explanation = j.optString("explanation"),
            actions = j.optJSONArray("recommended_actions").strings(),
            urlChecks = if (links == null) emptyList() else List(links.length()) {
                val c = links.getJSONObject(it)
                UrlCheck(
                    url = c.optString("url"),
                    malicious = c.optBoolean("malicious"),
                    threats = c.optJSONArray("threats").strings(),
                )
            },
            latencyMs = j.optLong("latency_ms"),
        )
    }
}
