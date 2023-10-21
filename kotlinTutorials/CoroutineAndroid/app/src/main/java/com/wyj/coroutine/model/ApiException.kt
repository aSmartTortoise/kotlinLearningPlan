package com.wyj.coroutine.model

import java.io.IOException

class ApiException(val errorCode:Int,val errorMsg:String): IOException()