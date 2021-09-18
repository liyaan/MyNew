package com.liyaan.rxImage

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class AbstractCacheObservable {
    fun requestImage(url:String): Observable<Image> {
        return Observable.create(ObservableOnSubscribe<Image> { emitter ->
            getImage(url)?.let { emitter.onNext(it) }
            emitter.onComplete()
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 保存图片
     * @param image
     */
    abstract fun putImage(image: Image?)


    /**
     * 具体获取 image方法
     * @param url
     * @return
     */
    abstract fun getImage(url: String?): Image?
}