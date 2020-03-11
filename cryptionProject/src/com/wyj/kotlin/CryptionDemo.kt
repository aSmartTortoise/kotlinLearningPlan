package com.wyj.kotlin

import com.wyj.kotlin.utils.HttpUtils
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter

fun main() {
    var urlStr: String = "https://zhuanlan.zhihu.com/api/columns/zhihuadmin"
//    var responseStr = HttpUtils.getRequest(urlStr)
    var keyStr: String = "1234567887654321"
//    var bw = BufferedWriter(FileWriter("adminInfo.json"))
//    bw.write(AESEncript.encript(keyStr, responseStr))
//    bw.close()

    var br = BufferedReader(FileReader("adminInfo.json"))
    var encryptionContent: String? = null
//    var decryptionContent = AESEncript.decript(keyStr, encryptionContent)
//    println("解密后的:$decryptionContent")
    var sb: StringBuilder = StringBuilder()
    while (true) {
        encryptionContent = br.readLine()
        if (encryptionContent != null) {
            sb.append(encryptionContent)
        }else {
            break
        }
    }
    br.close()
    var decript = AESEncript.decript(keyStr, sb.toString())
    println(decript)

}