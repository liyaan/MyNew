package com.liyaan.glide.load.model

import androidx.core.util.Pools
import java.util.ArrayList
import com.liyaan.glide.load.Options
class MultiModelLoaderFactory (val throwableListPool : Pools.Pool<List<Throwable>>?=null){

    private val entries = ArrayList<Entry<*, *>>()

    fun <Model, Data> append(
        modelClass: Class<Model>,
        dataClass: Class<Data>,
        factory: ModelLoaderFactory<out Model, out Data>
    ) {
        add(modelClass, dataClass, factory,  /*append=*/true)
    }

    private fun <Model, Data> add(
        modelClass: Class<Model>,
        dataClass: Class<Data>,
        factory: ModelLoaderFactory<out Model, out Data>,
        append: Boolean
    ) {
        val entry: Entry<Model, Data> =
            Entry(
                modelClass,
                dataClass,
                factory
            )
        entries.add(if (append) entries.size else 0, entry)
    }

    fun<Model, Data> build(modelClass:Class<Model> ,dataClass:Class<Data>):ModelLoader<Model, Data> {
        val loaders = ArrayList<ModelLoader<Model, Data>>()
        for (entry in entries){
            if(entry.handles(modelClass,dataClass)){
                loaders.add(build(entry))
            }
        }
        return loaders[0]
    }

    fun <Model, Data> build(entry: MultiModelLoaderFactory.Entry<*, *>): ModelLoader<Model, Data> {
        return entry.factory.build(this) as ModelLoader<Model, Data>
    }

    fun<Model> build(modelClass:Class<Model>):List<ModelLoader<Model, *>>{
        val loaders = ArrayList<ModelLoader<Model, *>>()
        entries.forEach {
            if (it.handles(modelClass)) {
                loaders.add(this.build<Model, Any>(it))
            }
        }
        return loaders
    }

    fun <Model> getDataClasses(modelClass: Class<Model>): List<Class<*>> {
        val result = ArrayList<Class<*>>()
        entries.forEach {entry->
            if (!result.contains(entry.dataClass) && entry.handles(modelClass)) {
                result.add(entry.dataClass)
            }
        }
        return result
    }

    class Factory {
        fun <Model, Data> build(
            modelLoaders: List<ModelLoader<Model, Data>>,
            throwableListPool: Pools.Pool<List<Throwable>>
        ): MultiModelLoader<Model, Data>? {
            return MultiModelLoader(modelLoaders, throwableListPool)
        }
    }

    class Entry<Model, Data>(
        val modelClass: Class<Model>,
        val dataClass: Class<Data>,
        val factory: ModelLoaderFactory<out Model, out Data>
    ) {

        fun handles(modelClass:Class<*>):Boolean{
            return this.modelClass.isAssignableFrom(modelClass)
        }
        fun handles(modelClass : Class<*> ,dataClass :Class<*>):Boolean{
            return handles(modelClass)&&this.dataClass.isAssignableFrom(dataClass)
        }
    }

    class EmptyModelLoader:ModelLoader<Any,Any>{
        override fun buildLoadData(
            model: Any,
            width: Int,
            height: Int,
            options: Options
        ): ModelLoader.LoadData<Any>? {
            return null
        }

        override fun handles(model: Any): Boolean {
            return false
        }

    }
}