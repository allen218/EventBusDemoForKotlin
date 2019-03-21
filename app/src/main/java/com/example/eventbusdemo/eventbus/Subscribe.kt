package com.example.eventbusdemo.eventbus

import java.lang.annotation.RetentionPolicy

/**
 * 描述:
 * 作者:王聪 001928
 * 创建日期：2019/3/15 on 15:26
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(val id: Array<String>)