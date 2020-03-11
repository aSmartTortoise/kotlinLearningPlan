package com.wyj.kotlin

import com.sun.org.apache.xml.internal.security.utils.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESEncript {
    fun encript(keyStr: String, originalContent: String): String {
        /**
         * 1.构建cipher对象
         * 2.初始化cipher
         * 3.加密
         */

        var cipher = Cipher.getInstance("AES")
        var keySpec = SecretKeySpec(keyStr.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        var encryptByteArray = cipher.doFinal(originalContent.toByteArray())
        return Base64.encode(encryptByteArray)
    }

    fun decript(keyStr: String, encryptContent: String): String {
        /**
         * 1.构建cipher对象
         * 2.初始化cipher
         * 3.解密
         */

        var cipher = Cipher.getInstance("AES")
        var keySpec = SecretKeySpec(keyStr.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        var decodeByteArray = Base64.decode(encryptContent.toByteArray())
        var decryptByteArray = cipher.doFinal(decodeByteArray)
        return String(decryptByteArray)
    }
}

fun main() {
    /**
     * 1.构建cipher对象
     * 2.初始化cipher
     * 3.加密/解密
     */
    var originalContent: String = "I LOVE YOU"
    var keyStr: String = "1234567887654321"
    var encriptContent = AESEncript.encript(keyStr, originalContent)
    println(encriptContent)

    println(AESEncript.decript(keyStr, encriptContent))

}