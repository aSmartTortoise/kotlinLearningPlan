package com.wyj.kotlin.utils

import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

object HttpUtils {

    fun getRequest(urlStr: String): String {
        var urlConnection = URL(urlStr).openConnection()
        var httpConnection = urlConnection as HttpURLConnection
        var inputStream = httpConnection.inputStream
        var baos = ByteArrayOutputStream()
        var buffer = ByteArray(1024)
        var length = inputStream.read(buffer)
        while (length != -1) {
            baos.write(buffer, 0, length)
            length = inputStream.read(buffer)
        }
        inputStream.close()
        baos.close()
        return baos.toString()
    }
}