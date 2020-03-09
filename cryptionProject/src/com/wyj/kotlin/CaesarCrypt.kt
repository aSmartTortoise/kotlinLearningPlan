package com.wyj.kotlin

import java.lang.StringBuilder

/**
 * 凯撒加密算法
 * 原理 将字符移动一定的位数来实现加密和解密
 */
class CaesarCryptKotlin {
    fun encrypt(input: String, key: Int): String {
        return with(StringBuilder()) {
            input.toCharArray().forEach {
                var c = (it.toInt() + key).toChar()
                append(c)
            }
            toString()
        }
    }

    fun decrypt(input: String, key: Int): String {
        return with(StringBuilder()) {
            input.toCharArray().forEach {
                var c = (it.toInt() - key).toChar()
                append(c)
            }
            toString()
        }
    }
}
fun main() {
//    var c: Char = 'A'
//    var key: Int = 3
//    var result = (c.toInt() + key).toChar()
//    println("result: $result")
    var content: String = "I LOVE YOU"
    var key: Int = 3
    var cryptKotlin = CaesarCryptKotlin()
    var encryptContent = cryptKotlin.encrypt(content, 3)
    println("凯撒加密后的密文:$encryptContent")

    var decrypt = cryptKotlin.decrypt(encryptContent, key)
    println("凯撒解密后的内容:$decrypt")


}