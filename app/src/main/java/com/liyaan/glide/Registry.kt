package com.liyaan.glide


import com.liyaan.glide.ext.printThis
import com.liyaan.glide.load.Encoder
import com.liyaan.glide.load.ResourceDecoder
import com.liyaan.glide.load.ResourceEncoder
import com.liyaan.glide.load.data.DataRewinder
import com.liyaan.glide.load.data.DataRewinderRegistry
import com.liyaan.glide.load.engine.DecodePath
import com.liyaan.glide.load.engine.LoadPath
import com.liyaan.glide.load.engine.Resource
import com.liyaan.glide.load.engine.resource.transcode.ResourceTranscoder
import com.liyaan.glide.load.model.ModelLoader
import com.liyaan.glide.load.model.ModelLoaderFactory
import com.liyaan.glide.load.model.ModelLoaderRegistry
import com.liyaan.glide.load.resource.transcode.TranscoderRegistry
import com.liyaan.glide.provider.EncoderRegistry
import com.liyaan.glide.provider.ResourceDecoderRegistry
import com.liyaan.glide.provider.ResourceEncoderRegistry
import java.util.ArrayList

class Registry {

    companion object {
        const val BUCKET_GIF = "Gif"
        const val BUCKET_BITMAP = "Bitmap"
        const val BUCKET_BITMAP_DRAWABLE = "BitmapDrawable"
    }

    private val modelLoaderRegistry = ModelLoaderRegistry()
    private val decoderRegistry = ResourceDecoderRegistry()
    private val transcoderRegistry = TranscoderRegistry()
    private val dataRewinderRegistry = DataRewinderRegistry()
    private val resourceEncoderRegistry = ResourceEncoderRegistry()
    private val encoderRegistry = EncoderRegistry()

    //model
    fun <Model, Data> append(
        modelClass: Class<Model>,
        dataClass: Class<Data>,
        factory: ModelLoaderFactory<Model, Data>
    ): Registry {
        modelLoaderRegistry.append(modelClass, dataClass, factory)
        return this
    }

    //decode
    fun <Data, TResource> append(
        bucket: String,
        dataClass: Class<Data>,
        resourceClass: Class<TResource>,
        decoder: ResourceDecoder<Data, TResource>
    ): Registry {
        decoderRegistry.append(bucket, decoder, dataClass, resourceClass)
        return this
    }

    //transcode
    fun <TResource, Transcode> register(
        resourceClass: Class<TResource>,
        transcodeClass: Class<Transcode>,
        transcoder: ResourceTranscoder<TResource, Transcode>
    ): Registry {
        transcoderRegistry.register(resourceClass, transcodeClass, transcoder)
        return this
    }

    //dataRewinder
    fun register(factory: DataRewinder.Factory<*>): Registry {
        dataRewinderRegistry.register(factory)
        return this
    }

    //encoder
    fun <TResource> append(
        resourceClass: Class<TResource>,
        encoder: ResourceEncoder<TResource>
    ): Registry {
        resourceEncoderRegistry.append(resourceClass, encoder)
        return this
    }

    //source encoder
    fun <Data> append(dataClass: Class<Data>, encoder: Encoder<Data>): Registry {
        encoderRegistry.append(dataClass, encoder)
        return this
    }

    fun <Model : Any> getModelLoaders(model: Model): List<ModelLoader<Model, *>> {
        return modelLoaderRegistry.getModelLoaders(model)
    }

    fun <Data, TResource, Transcode> getLoadPath(
        dataClass: Class<Data>,
        resourceClass: Class<TResource>,
        transcodeClass: Class<Transcode>
    ): LoadPath<Data, TResource, Transcode>? {
        var result: LoadPath<Data, TResource, Transcode>? = null
        val decodePaths = getDecodePaths(dataClass, resourceClass, transcodeClass);
        if (decodePaths.isNotEmpty()) {
            println("getLoadPath -> decodePaths size =${decodePaths.size}")
            result = LoadPath(dataClass, resourceClass, transcodeClass, decodePaths)
        }
        return result
    }

    private fun <Data, TResource, Transcode> getDecodePaths(
        dataClass: Class<Data>,
        resourceClass: Class<TResource>,
        transcodeClass: Class<Transcode>
    ): List<DecodePath<Data, TResource, Transcode>> {
        val decodePaths =
            ArrayList<DecodePath<Data, TResource, Transcode>>()
        //??????dataClass???inputStream??????,????????????size =2 ,resourceClass??? bitmap ???bitmapDrawable
        val registeredResourceClasses =
            decoderRegistry.getResourceClasses(dataClass, resourceClass);
        registeredResourceClasses.forEach { registeredResourceClass ->
            //registeredResourceClass???,bitmap ???bitmapDrawable
            //transcodeClass ???drawable
            val registeredTranscodeClasses =
                transcoderRegistry.getTranscodeClasses(registeredResourceClass, transcodeClass)
            registeredTranscodeClasses.forEach { registeredTranscodeClass ->
                //??????decoder????????? ,BitmapDrawableDecoder
                val decoders = decoderRegistry.getDecoders(dataClass, registeredResourceClass);
                val transcoder =
                    transcoderRegistry.get(registeredResourceClass, registeredTranscodeClass)
                val path = DecodePath(
                    dataClass,
                    registeredResourceClass,
                    registeredTranscodeClass,
                    decoders,
                    transcoder
                )
                decodePaths.add(path)
            }
        }
        return decodePaths
    }

    fun <Data : Any> getRewinder(data: Data): DataRewinder<Data> {
        return dataRewinderRegistry.build(data)
    }

    fun isResourceEncoderAvailable(resource: Resource<*>): Boolean {
        return resourceEncoderRegistry.get(resource.getResourceClass()) != null
    }

    fun <Z> getResultEncoder(resource: Resource<Z>?): ResourceEncoder<Z>? {
        val resourceEncoder: ResourceEncoder<Z>? =
            resourceEncoderRegistry.get(resource!!.getResourceClass())
        if (resourceEncoder != null) {
            return resourceEncoder
        }
        throw Exception("${resource.getResourceClass()}??????????????????resourceEncoder?????????!!")
    }


    fun <X> getSourceEncoder(data: X): Encoder<X> {
        val encoder: Encoder<X>? = encoderRegistry.getEncoder((data as Any).javaClass as Class<X>)
        if (encoder != null) {
            return encoder
        }
        throw Exception("????????????SourceEncoder")
    }

    fun <Model, TResource, Transcode> getRegisteredResourceClasses(
        modelClass: Class<Model>,
        resourceClass: Class<TResource>,
        transcodeClass: Class<Transcode>
    ): List<Class<*>> {
        val result = arrayListOf<Class<*>>()

        val dataClasses = modelLoaderRegistry.getDataClasses(modelClass);
        for (dataClass in dataClasses) {
            val registeredResourceClasses =
                decoderRegistry.getResourceClasses(dataClass, resourceClass);
            for (registeredResourceClass in registeredResourceClasses) {
                val registeredTranscodeClasses =
                    transcoderRegistry.getTranscodeClasses(registeredResourceClass, transcodeClass);
                if (registeredTranscodeClasses.isNotEmpty() && !result.contains(
                        registeredResourceClass
                    )
                ) {
                    printThis(" result.add() ,registeredResourceClass =${ registeredResourceClass.simpleName}")
                    result.add(registeredResourceClass)
                }
            }
        }

        return result
    }
}