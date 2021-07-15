package com.liyaan.eventBus

import java.lang.StringBuilder
import java.lang.reflect.Method

class SubscriberMethod(methos: Method,eventType:Class<*>,threadMode:ThreadMode,priority:Int,sticky:Boolean){
    val method = methos
    val threadMode = threadMode
    val eventType = eventType
    val priority = priority
    val sticky = sticky
    var methodString:String? = null

    override fun equals(other: Any?): Boolean {
        if (other==this){
            return true
        }else if (other is SubscriberMethod){
            checkMethodString()
            val otherSubscriberMethod = other as SubscriberMethod
            otherSubscriberMethod.checkMethodString()
            return methodString.equals(otherSubscriberMethod.methodString)
        }else{
            return false
        }
    }
    @Synchronized
    private fun checkMethodString(){
        if (methodString==null){
            val builder = StringBuilder(64)
            builder.append(method.declaringClass.name)
            builder.append('#').append(method.name)
            builder.append('(').append(eventType.name)
            methodString = builder.toString();
        }
    }

    override fun hashCode(): Int {
        return method.hashCode()
    }
}