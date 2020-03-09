package com.wyj.kotlin

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

object DESEncryption {//object 表明该类是单例的
    /**
     * 1.创建cipher
     * 2.初始化cipher
     * 3.加密/解密
     */
    fun encryption(keyStr: String, originalContent: String): ByteArray {
        //1创建cipher
        var cipher = Cipher.getInstance("DES")
        //2初始化cipher
        var skf = SecretKeyFactory.getInstance("DES")
        var desKeySpec = DESKeySpec(keyStr.toByteArray())
        var key = skf.generateSecret(desKeySpec)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        //3.加密
        return cipher.doFinal(originalContent.toByteArray())
    }

    fun decryption(keyStr: String, encryptionContent: ByteArray): ByteArray {
        //1创建cipher
        var cipher = Cipher.getInstance("DES")
        //2初始化cipher
        var skf = SecretKeyFactory.getInstance("DES")
        var desKeySpec = DESKeySpec(keyStr.toByteArray())
        var key = skf.generateSecret(desKeySpec)
        cipher.init(Cipher.DECRYPT_MODE, key)
        //3.加密
        return cipher.doFinal(encryptionContent)
    }
}

fun main() {

    var keyStr: String = "12345678"
    var originalContent: String = "I LOVE YOU"

    var encryption = DESEncryption.encryption(keyStr, originalContent)
    println(encryption)

    var decryption = DESEncryption.decryption(keyStr, encryption)
    println("解密后的内容:" + String(decryption))

}