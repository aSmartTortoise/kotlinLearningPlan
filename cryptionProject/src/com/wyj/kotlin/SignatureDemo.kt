package com.wyj.kotlin

import com.sun.org.apache.xml.internal.security.utils.Base64
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature

object SignatureDemo {
    /**
     * 数字签名  加签
     */

    fun sign(originalContent: String, privateKey: PrivateKey): String {
        var signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(originalContent.toByteArray())
        var signArray = signature.sign()
        return Base64.encode(signArray)
    }

    fun verifySign(originalContent: String, signContent: String, publicKey: PublicKey): Boolean {
        var signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(originalContent.toByteArray())
        return signature.verify(Base64.decode(signContent))
    }
}

fun main() {
    var privateKey = RSACrypt.getPrivateKey()
    var publicKey = RSACrypt.getPublicKey()
    var signContent = SignatureDemo.sign("price=12369", privateKey)
    println(signContent)
    var verifySign = SignatureDemo.verifySign("price=9", signContent, publicKey)
    println("verifySing:$verifySign")
}