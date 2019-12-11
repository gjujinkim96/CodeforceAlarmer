package com.example.codeforcealarmer

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class HttpHandler {
    companion object {
        fun fetchFromUrl(url: String): String? {
            val urlObject =
                try {
                    URL(url)
                } catch (e: MalformedURLException) {
                    Log.e(this::class.java.simpleName, e.toString())
                    throw IllegalArgumentException()
                }

            var httpConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            var ret: String? = null

            try
            {
                httpConnection = urlObject.openConnection() as? HttpURLConnection ?: throw IOException()

                httpConnection.apply {
                    connectTimeout = 5000
                    readTimeout = 5000
                    requestMethod = "GET"
                    connect()
                }

                if (httpConnection.responseCode == 200) {
                    ret = readText(httpConnection.inputStream)
                } else {
                    Log.v(this::class.java.simpleName, "ResponseCode is not 200")
                }
            }catch (e : IOException)
            {
                Log.e(this::class.java.simpleName, e.toString())
                ret = null
            }finally
            {
                try {
                    inputStream?.close()
                } catch (e: IOException) {
                    Log.v(this::class.java.simpleName, "inputstream failed to close")
                }
                httpConnection?.disconnect()
            }

            return ret
        }

        private fun readText(inputStream: InputStream): String {
            val sb = StringBuilder()
            val br = BufferedReader(InputStreamReader(inputStream, "utf-8"))
            try {
                while (true) {
                    val line = br.readLine() ?: break
                    sb.append(line)
                }
            } catch (e: IOException) {
                Log.e(this::class.java.simpleName, e.toString())
            } finally {
                try {
                    br.close()
                } catch (e: IOException) {
                    Log.v(this::class.java.simpleName, "BufferedReader failed to close")
                }
            }

            return sb.toString()
        }
    }
}