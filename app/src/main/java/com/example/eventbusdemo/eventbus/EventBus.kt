package com.example.eventbusdemo.eventbus

import android.util.Log
import com.example.eventbusdemo.info.SubscribeMethod
import com.example.eventbusdemo.info.Subscription
import java.lang.reflect.Method

/**
 * 描述:
 * 作者:王聪 001928
 * 创建日期：2019/3/15 on 17:07
 */
object EventBus {
    val methodCaches: MutableMap<Class<Any>, MutableList<SubscribeMethod>> = mutableMapOf()
    val subscribes: MutableMap<String, MutableList<Subscription>> = mutableMapOf()
    val registerLabels: MutableMap<Class<Any>, MutableList<String>> = mutableMapOf()

    fun register(obj: Any) {
        val javaClass = obj.javaClass
        var subscribeMethods = methodCaches[javaClass]
        if (subscribeMethods == null) {
            subscribeMethods = mutableListOf()
            methodCaches[javaClass] = subscribeMethods
        }

        obj.javaClass.declaredMethods.forEach { method: Method ->
            val subscribe = method.getAnnotation(Subscribe::class.java)
            if (subscribe != null) {
                val ids = subscribe.id
                val parameterTypes: Array<Class<Any>> = method.parameterTypes as Array<Class<Any>>
                method.isAccessible = true
                ids.forEach {
                    subscribeMethods.add(SubscribeMethod(it, method, parameterTypes))
                }
            }
        }

        var labels = registerLabels[javaClass]
        if (null == labels) {
            labels = mutableListOf()
            registerLabels.put(javaClass, labels)
        }

        // cache current page's labels for clear when current page close.
        val iterator = methodCaches.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val subscribeMethods = entry.value
            subscribeMethods.forEach {
                if (!labels.contains(it.label)) {
                    labels.add(it.label)
                }

                var subscriptions = subscribes[it.label]
                if (null == subscriptions) {
                    subscriptions = mutableListOf()
                    subscribes.put(it.label, subscriptions)
                }
                subscriptions.add(Subscription(it, obj))
            }
        }
    }

    fun post(label: String, vararg obj: Any?) {
        val subscriptions = subscribes[label]
        if (null == subscriptions) {
            return
        }

        subscriptions.forEach {
            val subscribeMethod = it.subscribeMethod
            val paramsClz = subscribeMethod.paramsClz

            val realParams = arrayOfNulls<Any?>(paramsClz.size)
            for ((index, value) in obj.withIndex()) {
                val clzParam = paramsClz[index]
                if (clzParam.isInstance(value)) {
                    realParams[index] = value
                } else {
                    realParams[index] = null
                }
            }
            try {
                subscribeMethod.method.invoke(it.obj, *realParams)
            } catch (ex: Exception) {
                Log.e("TAG", "invoke exception: $ex")
            }
        }
    }

    fun unRegister(obj: Any) {
        val labels = registerLabels[obj.javaClass]
        labels?.forEach {
            val subscriptions = subscribes[it]
            val subscriptionIterator = subscriptions!!.iterator()
            while (subscriptionIterator.hasNext()) {
                val entity = subscriptionIterator.next()
                if (entity.obj === obj) {
                    subscriptionIterator.remove()
                }
            }

        }
    }

}