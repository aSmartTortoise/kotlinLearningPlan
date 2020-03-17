package com.wyj.kotlin

import com.sun.org.apache.xml.internal.security.utils.Base64
import java.io.ByteArrayOutputStream
import java.security.Key
import java.security.KeyPairGenerator
import javax.crypto.Cipher

/**
 * 非对称加密
 */
object RSACrypt {
    val transformation: String = "RSA"
    val ENCRYPT_MAX_SIZE: Int = 117
    val DECRYPT_MAX_SIZE: Int = 128
    /**
     * 私钥加密
     */
    fun encryptByPrivateKey(originalContent: String, privateKey: Key): String {
        var cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, privateKey)

        var byteArrayContent = originalContent.toByteArray()
        var offset: Int = 0
        var tempByteArray: ByteArray? = null
        var baos = ByteArrayOutputStream()
        while (byteArrayContent.size - offset > 0) {
            if (byteArrayContent.size - offset >= ENCRYPT_MAX_SIZE) {
                tempByteArray = cipher.doFinal(byteArrayContent, offset, ENCRYPT_MAX_SIZE)
                offset += ENCRYPT_MAX_SIZE
            }else {
                tempByteArray = cipher.doFinal(byteArrayContent, offset, byteArrayContent.size - offset)
                offset = byteArrayContent.size
            }
            baos.write(tempByteArray)
        }
        baos.close()
        return Base64.encode(baos.toByteArray())
    }

    /**
     * 公钥加密
     */
    fun encryptByPublicKey(originalContent: String, publicKey: Key): String {
        var cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        var byteArrayContent = originalContent.toByteArray()
        var offset: Int = 0
        var tempByteArray: ByteArray? = null
        var baos = ByteArrayOutputStream()
        while (byteArrayContent.size - offset > 0) {
            if (byteArrayContent.size - offset >= ENCRYPT_MAX_SIZE) {
                tempByteArray = cipher.doFinal(byteArrayContent, offset, ENCRYPT_MAX_SIZE)
                offset += ENCRYPT_MAX_SIZE
            }else {
                tempByteArray = cipher.doFinal(byteArrayContent, offset, byteArrayContent.size - offset)
                offset = byteArrayContent.size
            }
            baos.write(tempByteArray)
        }
        baos.close()
        return Base64.encode(baos.toByteArray())
    }

    /**
     * 私钥解密
     */
    fun decryptByPrivateKey(encryptContent: String, privateKey: Key): String {
        var cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        var byteArrayContent = Base64.decode(encryptContent)
        var offset: Int = 0
        var tempByteArray: ByteArray? = null
        var baos = ByteArrayOutputStream()
        while (byteArrayContent.size - offset > 0) {
            if (byteArrayContent.size - offset >= DECRYPT_MAX_SIZE) {
                tempByteArray = cipher.doFinal(byteArrayContent, offset, DECRYPT_MAX_SIZE)
                offset += DECRYPT_MAX_SIZE
            }else {
                tempByteArray = cipher.doFinal(byteArrayContent, offset, byteArrayContent.size - offset)
                offset = byteArrayContent.size
            }
            baos.write(tempByteArray)
        }
        baos.close()
        return String(baos.toByteArray())
    }

    /**
     * 公钥解密
     */
    fun decryptByPublicKey(encryptContent: String, publcKey: Key): String {
        var cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, publcKey)

        var byteArrayContent = Base64.decode(encryptContent)
        var offset: Int = 0
        var tempByteArray: ByteArray? = null
        var baos = ByteArrayOutputStream()
        while (byteArrayContent.size - offset > 0) {
            if (byteArrayContent.size - offset >= DECRYPT_MAX_SIZE) {
                tempByteArray = cipher.doFinal(byteArrayContent, offset, DECRYPT_MAX_SIZE)
                offset += DECRYPT_MAX_SIZE
            }else {
                tempByteArray = cipher.doFinal(byteArrayContent, offset, byteArrayContent.size - offset)
                offset = byteArrayContent.size
            }
            baos.write(tempByteArray)
        }
        baos.close()
        return String(baos.toByteArray())
    }
}

fun main() {
    var keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    var keyPair = keyPairGenerator.genKeyPair()
    keyPair?.let {
        var encodedPublicKey = Base64.encode(keyPair.public.encoded)
        var encodedPrivateKey = Base64.encode(keyPair.private.encoded)
//        println("公钥：$encodedPublicKey , 私钥: $encodedPrivateKey")
        var originalContent: String = "别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？"
        var encryptByPrivateKey = RSACrypt.encryptByPrivateKey(originalContent, keyPair.private)
        println("私钥加密后：$encryptByPrivateKey")
        var encryptByPublicKey = RSACrypt.encryptByPublicKey(originalContent, keyPair.public)
        println("公钥加密后：$encryptByPublicKey")

        var decryptByPrivateKey = RSACrypt.decryptByPrivateKey(encryptByPublicKey, keyPair.private)
        println("私钥解密后的:$decryptByPrivateKey")

        var decryptByPublicKey = RSACrypt.decryptByPublicKey(encryptByPrivateKey, keyPair.public)
        println("公钥解密后:$decryptByPublicKey")
    }


}