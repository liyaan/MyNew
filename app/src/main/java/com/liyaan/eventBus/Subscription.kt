package com.liyaan.eventBus

class Subscription(subscriber:Any,subscriberMethod:SubscriberMethod) {
    val subscriber = subscriber
    val subsciberMethod = subscriberMethod
    @Volatile
    var active:Boolean = true

    override fun equals(other: Any?): Boolean {
        if (other is Subscription){
            val otherSub = other as Subscription
            return  subscriber == otherSub.subscriber && subsciberMethod == otherSub.subsciberMethod
        }else{
            return false
        }
    }

    override fun hashCode(): Int {
        return subscriber.hashCode()+subsciberMethod.methodString.hashCode()
    }
}