package com.wyj.kotlin

import java.lang.StringBuilder
import java.security.MessageDigest

object MessageDigestUtils {

    fun md5Encrypt(originalContent: String): String {
        var messageDigest = MessageDigest.getInstance("MD5")
        var digestArray = messageDigest.digest(originalContent.toByteArray())
        return toHexStr(digestArray)
    }

    fun sha1Encrypt(originalContent: String): String {
        var messageDigest = MessageDigest.getInstance("SHA-1")
        var digestArray = messageDigest.digest(originalContent.toByteArray())
        println("digestArray的长度:${digestArray.size}")
        var result = toHexStr(digestArray)
        println("result的长度:${result.length}")
        return result
    }

    fun sha256Encrypt(originalContent: String): String {
        var messageDigest = MessageDigest.getInstance("SHA-256")
        var digestArray = messageDigest.digest(originalContent.toByteArray())
        println("digestArray的长度:${digestArray.size}")
        var result = toHexStr(digestArray)
        println("result的长度:${result.length}")
        return result
    }

    /**
     * 将字节数组转成对应的16进制的字符串
     */
    private fun toHexStr(array: ByteArray): String {
        return with(StringBuilder()) {
            array.forEach {
                var hex: Int = it.toInt() and (0xFF)//将byte转成int类型后通过位运算转成16进制
                var hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) {
                    append("0").append(hexStr)
                } else {
                    append(hexStr)
                }
            }
            toString()
        }
    }

}

fun main() {
    var originalContent = "I LOVE YOU I LOVE YOU I LOVE YOU"
    println("md5摘要后的:${MessageDigestUtils.md5Encrypt(originalContent)}")
    println("sha1摘要加密后的：${MessageDigestUtils.sha1Encrypt(originalContent)}")
    println("sha256摘要加密后的：${MessageDigestUtils.sha256Encrypt(originalContent)}")
}