package com.wyj.kotlin

import com.sun.org.apache.xml.internal.security.utils.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

object DESEncryption {//object 表明该类是单例的


    /**
     * 1.创建cipher
     * 2.初始化cipher
     * 3.加密/解密
     * 加解密中出现异常，乱码等的解决方案
     * javax.crypto.IllegalBlockSizeException: Input length must be multiple of 8 when decrypting with padded cipher
     * https://facingissuesonit.com/2017/10/30/exception-javax-crypto-illegalblocksizeexception-input-length-must-be-multiple-of-16-when-decrypting-with-padded-cipher/
     */
    fun encryption(keyStr: String, originalContent: String): String {
        //1创建cipher
        var cipher = Cipher.getInstance("DES")
        //2初始化cipher
        var skf = SecretKeyFactory.getInstance("DES")
        var desKeySpec = DESKeySpec(keyStr.toByteArray())
        var key = skf.generateSecret(desKeySpec)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        //3.加密
        var resultByteArray = cipher.doFinal(originalContent.toByteArray())
        var result = Base64.encode(resultByteArray)
        return result
    }

    fun decryption(keyStr: String, encryptionContent: String): ByteArray {
        //1创建cipher
        var cipher = Cipher.getInstance("DES")
        //2初始化cipher
        var skf = SecretKeyFactory.getInstance("DES")
        var desKeySpec = DESKeySpec(keyStr.toByteArray())
        var key = skf.generateSecret(desKeySpec)
        cipher.init(Cipher.DECRYPT_MODE, key)
        //3.加密
        var base64ByteArray = Base64.decode(encryptionContent.toByteArray())
        return cipher.doFinal(base64ByteArray)
    }
}

fun main() {

    var keyStr: String = "123456789"
    var originalContent: String = "I LOVE YOU"

    var encryption = DESEncryption.encryption(keyStr, originalContent)
    println(encryption)

    var decryption = DESEncryption.decryption(keyStr, encryption)
    println("解密后的内容:" + String(decryption))

}