package com.kotlin.kt05

/**
 * a_b_c_d_e_f_g_h
 */
fun main(vararg args: String) {
    args.flatMap {
        it.split("_")
    }.map {
        print("$it ")
    }
}