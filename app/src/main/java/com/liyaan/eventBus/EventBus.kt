package com.liyaan.eventBus

import android.os.Handler
import android.os.Looper
import java.util.concurrent.CopyOnWriteArrayList


class EventBus {
    // subscriptionsByEventType 这个集合存放的是？
    // key 是 Event 参数的类(本例中是String.class)
    // value 存放的是 Subscription 的集合列表
    // Subscription 包含两个属性，一个是 subscriber 订阅者（反射执行对象,本例中是MainActivity.class），
    //      一个是 SubscriberMethod 注解方法的所有属性参数值
    private var subscriptionsByEventType:HashMap<Class<*>, CopyOnWriteArrayList<Subscription>>? = null
    // typesBySubscriber 这个集合存放的是？
    // key 是所有的订阅者(MainActivity.class)
    // value 是所有订阅者里面方法的参数的class() (本例中就是 String.class 的集合)
    private var typesBySubscriber:HashMap<Any, MutableList<Class<*>>>? = null

    init {
        subscriptionsByEventType = HashMap()
        typesBySubscriber = HashMap()
    }
    companion object{
        @Volatile
        @JvmStatic
        var INSTANCE:EventBus? = null
        fun getDefault():EventBus = INSTANCE?: synchronized(this){
            INSTANCE?:EventBus().also { INSTANCE = it }
        }
    }

    fun register(obj:Any){
        val subscriberMethods = ArrayList<SubscriberMethod>()
        val objClass = obj.javaClass
        val dexlereMethods = objClass.declaredMethods
        dexlereMethods.forEach {
            val annotation= it.getAnnotation(Subscribe::class.java)
            if (annotation!=null){
                val parameterTypes = it.parameterTypes
                val model = SubscriberMethod(it,parameterTypes[0],annotation.threadMode,annotation.priority,annotation.sticky)
                subscriberMethods.add(model)
            }
        }

        subscriberMethods.forEach {
            subscribe(obj,it)
        }
    }

    private fun subscribe(obj: Any, method: SubscriberMethod) {
        val eventType = method.eventType
        var subscriptions = subscriptionsByEventType?.get(eventType)
        if (subscriptions==null){
            subscriptions = CopyOnWriteArrayList()
            subscriptionsByEventType?.put(eventType,subscriptions)
        }
        val subscription = Subscription(obj,method)
        subscriptions.add(subscription)

        var eventTypeList = typesBySubscriber?.get(obj)
        if (eventTypeList==null){
            eventTypeList = ArrayList()
            typesBySubscriber!!.put(obj,eventTypeList)
        }
        if (!eventTypeList.contains(eventType)){
            eventTypeList.add(eventType)
        }
    }

    fun unregister(obj:Any){
        val eventList = typesBySubscriber?.get(obj)
        if (eventList!=null){
            eventList.forEach {
                removeObject(it,obj)
            }
        }
    }

    private fun removeObject(eventType: Class<*>, obj: Any) {
        val subscriptionList = subscriptionsByEventType?.get(eventType)
        if (subscriptionList!=null){
            var size = subscriptionList.size
            var x = 0
            while (x<size){
                val subscription = subscriptionList[x]
                if (subscription.subscriber==obj){
                    subscriptionList.removeAt(x)
                    x -= 1
                    size--
                }
                x++
            }
        }
    }

    fun post(obj:Any){
        val evenType = obj.javaClass
        val subPtions = subscriptionsByEventType?.get(evenType)
        if (subPtions!=null){
            subPtions.forEach {
                executeMothod(it,obj)
            }
        }
    }

    private fun executeMothod(subscription: Subscription?, obj: Any) {
        val subMethod = subscription?.subsciberMethod
        val isMainThread = Looper.getMainLooper() == Looper.myLooper()
        when(subMethod?.threadMode){
            ThreadMode.POSTING->{
                invokeMethod(subscription,obj)
            }
            ThreadMode.MAIN->{
                if (isMainThread){
                    invokeMethod(subscription,obj)
                }else{
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        invokeMethod(subscription,obj)
                    }
                }
            }
            ThreadMode.BACKGROUND->{
                if (isMainThread){
                    AsyncPoster.enqueue(subscription,obj)
                }else{
                    invokeMethod(subscription,obj)
                }
            }
            ThreadMode.ASYNC->{
                AsyncPoster.enqueue(subscription,obj)
            }
        }
    }

    private fun invokeMethod(subscription: Subscription, obj: Any) {
        subscription.subsciberMethod.method.invoke(subscription.subscriber,obj)
    }
}