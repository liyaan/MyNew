package com.liyaan.rxJava

class Observable<T> private constructor(private val onsubscribe: OnSubscribe<T>){
    val onSubscribe = onsubscribe

    companion object{

        @JvmStatic
        fun <T> create(onSubscribe: OnSubscribe<T>): Observable<T> {
            return Observable<T>(onSubscribe)
        }

    }
    fun subscribe(subscriber: Subscriber<in T>){
        subscriber.onStart();
        onSubscribe.call(subscriber)
    }
    fun subscribeOn(scheduler:Scheduler):Observable<T>{
        return create(object:OnSubscribe<T>{
            override fun call(subscriber: Subscriber<in T>) {
                subscriber.onStart()
                scheduler.createWorker().schedule(Runnable { onSubscribe.call(subscriber) })
            }

        })
    }
    fun observeOn(scheduler:Scheduler):Observable<T>{
        return create(object:OnSubscribe<T>{
            override fun call(subscriber: Subscriber<in T>) {
                subscriber.onStart()
                val worker = scheduler.createWorker()
                onSubscribe.call(object:Subscriber<T>(){
                    override fun onCompleted() {
                        worker.schedule(Runnable { subscriber.onCompleted() })
                    }

                    override fun onError(t: Throwable) {
                        worker.schedule(Runnable { subscriber.onError(t) })
                    }

                    override fun onNext(t: T) {
                        worker.schedule(Runnable { subscriber.onNext(t) })
                    }

                })
            }

        })
    }

    fun observeOnMain(scheduler: LooperScheduler<Any>):Observable<T>{
        return create(object:OnSubscribe<T>{
            override fun call(subscriber: Subscriber<in T>) {
                subscriber.onStart()
                val worker = scheduler.createWorker(subscriber as Subscriber<Any>)
                onSubscribe.call(object:Subscriber<T>(){
                    override fun onCompleted() {
                    }

                    override fun onError(t: Throwable) {
                    }

                    override fun onNext(t: T) {
                        worker.sendContent(t)
//                        subscriber.onNext(t)
                    }

                })
            }

        })
    }
//    fun <R> map(transformer:Transformer<in T, in R>):Observable<R>{
//        return create(object:OnSubscribe<R>{
//            override fun call(subscriber: Subscriber<in R>) {
//                subscribe(object:Subscriber<T>(){
//                    override fun onCompleted() {
//                        subscriber.onCompleted()
//                    }
//
//                    override fun onError(t: Throwable) {
//                        subscriber.onError(t)
//                    }
//
//                    override fun onNext(t: T) {
//                        subscriber.onNext(transformer.call(t) as R)
//                    }
//
//                })
//            }
//
//        })
//    }
    fun <R> map(transformer:Transformer<in T,in R>):Observable<R>{
        return create(MapOnSubscribe(this,transformer))
    }
    inner class MapOnSubscribe<T,R>(source:Observable<T>,transformer:Transformer<in T,in R>):OnSubscribe<R>{
        private val source:Observable<T> = source
        private val transformer:Transformer<in T,in R> = transformer
        override fun call(subscriber: Subscriber<in R>) {
            source.subscribe(MapSubscriber(subscriber,transformer))
        }

    }
    inner class MapSubscriber<T,R>(actual:Subscriber<in T>,transformer:Transformer<in R,in T>): Subscriber<R>() {
        private var actual:Subscriber<in T> = actual
        private var transformer:Transformer<in R,in T> = transformer

        override fun onCompleted() {
            actual.onCompleted()
        }

        override fun onError(t: Throwable) {
            actual.onError(t)
        }

        override fun onNext(t: R) {
            actual.onNext(transformer.call(t) as T)
        }

    }
}