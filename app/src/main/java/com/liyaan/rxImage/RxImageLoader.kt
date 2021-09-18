package com.liyaan.rxImage

import android.content.Context
import android.widget.ImageView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate

class RxImageLoader(builder:Builder) {

    var mUrl: String? = null
    var requestCreator: RequestCreator? = null

    init {
        context = builder.context
        requestCreator = RequestCreator(context)
    }

    companion object{
        lateinit var context: Context
        var singletoon: RxImageLoader? = null
        fun with(context: Context):RxImageLoader?{
            if (singletoon==null){
                synchronized(RxImageLoader::class){
                    if (singletoon==null){
                        singletoon = Builder(context).build()
                    }
                }
            }
            return singletoon
        }
    }

    fun load(url: String): RxImageLoader? {
        mUrl = url
        return singletoon
    }

    fun into(imageView:ImageView):RxImageLoader?{
        Observable.concat(requestCreator!!.getImageFromMemory(mUrl!!)
            ,requestCreator!!.getImageFromDisk(mUrl!!)
            ,requestCreator!!.getImageFromNetwork(mUrl!!)).filter(object:Predicate<Image>{
            override fun test(image: Image): Boolean {
                return image.bitmap != null
            }

        }).firstElement().toObservable().subscribe(object:Observer<Image>{
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(image: Image) {
                imageView.setImageBitmap(image.bitmap)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

        })
        return singletoon
    }
    class Builder(context: Context){
        var context = context
        fun build(): RxImageLoader? {
            return RxImageLoader(this)
        }
    }
}