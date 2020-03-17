package com.wyj.kotlin

import java.lang.StringBuilder
import java.security.MessageDigest

object MessageDigestUtils {

    fun md5Encrypt(originalContent: String): String {
        var messageDigest = MessageDigest.getInstance("MD5")
        var digestArray = messageDigest.digest(originalContent.toByteArray())
        return toHexStr(digestArray)
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
    println("md5摘要后的:${MessageDigestUtils.md5Encrypt("I LOVE YOU")}")
}