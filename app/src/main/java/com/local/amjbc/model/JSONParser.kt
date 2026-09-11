package com.local.amjbc.model

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class JSONParser {

    fun nmakeHttpRequest(urlString: String, method: String,
                         params: List<Map.Entry<String, String>>): JSONObject? {

        var conn: HttpURLConnection? = null
        var inputStream: InputStream? = null
        val response = StringBuilder()
        var jsonObject: JSONObject? = null

        try {
            val url: URL

            if ("GET".equals(method, ignoreCase = true)) {
                val paramString = getQuery(params)
                url = URL("$urlString?$paramString")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("User-Agent", "Mozilla/5.0")
            } else if ("POST".equals(method, ignoreCase = true)) {
                url = URL(urlString)
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val os: OutputStream = conn.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                writer.write(getQuery(params))
                writer.flush()
                writer.close()
                os.close()
            } else {
                throw UnsupportedOperationException("Unsupported request method: $method")
            }

            conn.connect()
            inputStream = conn.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            jsonObject = JSONObject(response.toString())

        } catch (e: Exception) {
            Log.e("JSONParser", "Error making HTTP request: " + e.message, e)
        } finally {
            if (inputStream != null) try {
                inputStream.close()
            } catch (ignored: Exception) {
            }
            conn?.disconnect()
        }

        return jsonObject
    }

    @Throws(Exception::class)
    private fun getQuery(params: List<Map.Entry<String, String>>): String {
        val result = StringBuilder()
        var first = true

        for (entry in params) {
            if (first) first = false else result.append("&")

            result.append(URLEncoder.encode(entry.key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(entry.value, "UTF-8"))
        }

        return result.toString()
    }

    companion object {
        var `is`: InputStream? = null
        var jObj: JSONObject? = null
        var json: String = ""
    }
}
