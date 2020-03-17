package com.wyj.kotlin

import com.sun.org.apache.xml.internal.security.utils.Base64
import java.io.ByteArrayOutputStream
import java.security.*
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
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
            } else {
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
            } else {
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
            } else {
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
            } else {
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
//    var keyPairGenerator = KeyPairGenerator.getInstance("RSA")
//    var keyPair = keyPairGenerator.genKeyPair()
//    keyPair?.let {
//        var encodedPublicKey = Base64.encode(keyPair.public.encoded)
//        var encodedPrivateKey = Base64.encode(keyPair.private.encoded)
//        println("公钥：$encodedPublicKey , 私钥: $encodedPrivateKey")

    /**
     * 由于KeyPairGenerator每次生成的 的密钥对不尽相同，故开发中需要将生成的密钥对加以保存
     * 这里仅做示例演示
     */
    var encodedPublicKey: String = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkuMN8h0Wjec/vQ9ebxiXmlXqgjr2jy45LokmD\n" +
            "4qsEeKYyQd6BU2gGzsRKYHBL4HXcuoXlPaKcV7+J5rYw7wdVYS9FtxmL5+NZ1rTK3k2pbNPQtlD2\n" +
            "gkjey6R/oWQHQgjug7vnyvDEGbFoA6VJ/2qIUpdXFHV77OljMxdAePuU2wIDAQAB"
    var encodedPrivateKey: String = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKS4w3yHRaN5z+9D15vGJeaVeqCO\n" +
            "vaPLjkuiSYPiqwR4pjJB3oFTaAbOxEpgcEvgddy6heU9opxXv4nmtjDvB1VhL0W3GYvn41nWtMre\n" +
            "Tals09C2UPaCSN7LpH+hZAdCCO6Du+fK8MQZsWgDpUn/aohSl1cUdXvs6WMzF0B4+5TbAgMBAAEC\n" +
            "gYADxIIcKA/stE2QQHH/CyI0yvh6Eam+xFol2rlpvdaBjKzoe182gAziEvqkZN5Mrf6kJNQJMUa1\n" +
            "4r/rzI4gQmddgCoUVFcM/xt7nuz9LgfWjdENe1DKBmf/ShqyMlQDV5tsTrlYa4Ryw1twuXUzsQM8\n" +
            "8BCgrhbynu7SpgT5MVEbYQJBANj1VUaRyBdbcJ5DbU8Yq4G1umGngxYs664Omj9qBgcXsOHdeStH\n" +
            "DaQNaeOv0p9bJnnmALhNeUbju3lH/d6J1usCQQDCXQkhkJaH3gfkJNZ3+lB24heiUh+0254MrkQp\n" +
            "aZStvzd1hnSf2zDxQD4vV3gFjva3H4jJ3Gdt7iixKhRFr53RAkEAri8Jb8bK5jW7jNSFheNAjrrg\n" +
            "EFb0n3EhJnUC0bbFcBxNHok5Js283eEHCo22g8oicet+2HkazRc5BH4QDAKOUQJAJU48KXAaJHvu\n" +
            "YdDcRW0LMrZUuPgwU8Nvg5mTRauZOPwhxfIHwoMM0tF03htY0yBMpHtuAujGBWiX8OFVwMZjcQJA\n" +
            "avfPXsisHksJgJp5kf+41LC4W15UHkt806lgmvnRYqyc8j9H1htu2fYW+p3IxUgW6rNKVcW3qq3A\n" +
            "EmSRsM1FGA=="
    var originalContent: String =
        "别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？别问我从哪里来？"
    var privateKey: PrivateKey? = null
    var publicKey: PublicKey? = null
    //通过秘钥工厂生成秘钥
    var kf = KeyFactory.getInstance("RSA")
    privateKey = kf.generatePrivate(PKCS8EncodedKeySpec(Base64.decode(encodedPrivateKey)))
    var encryptByPrivateKey = RSACrypt.encryptByPrivateKey(originalContent, privateKey)
    println("私钥加密后：$encryptByPrivateKey")
    publicKey = kf.generatePublic(X509EncodedKeySpec(Base64.decode(encodedPublicKey)))
    var encryptByPublicKey = RSACrypt.encryptByPublicKey(originalContent, publicKey)
    println("公钥加密后：$encryptByPublicKey")
//
        var decryptByPrivateKey = RSACrypt.decryptByPrivateKey(encryptByPublicKey, privateKey)
        println("私钥解密后的:$decryptByPrivateKey")

        var decryptByPublicKey = RSACrypt.decryptByPublicKey(encryptByPrivateKey, publicKey)
        println("公钥解密后:$decryptByPublicKey")
//    }


}