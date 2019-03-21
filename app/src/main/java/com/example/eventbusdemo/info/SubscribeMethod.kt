package com.example.eventbusdemo.info

import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * 描述:
 * 作者:王聪 001928
 * 创建日期：2019/3/15 on 17:09
 */
data class SubscribeMethod(val label: String, val method: Method, val paramsClz: Array<Class<Any>>)