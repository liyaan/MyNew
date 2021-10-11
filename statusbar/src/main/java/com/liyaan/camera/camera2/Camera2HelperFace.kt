package com.liyaan.camera.camera2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.Face
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView
import com.liyaan.camera.util.BitmapUtils
import com.liyaan.camera.util.log
import com.liyaan.camera.util.toast
import com.liyaan.camera.view.AutoFitTextureView
import java.util.*
import kotlin.collections.ArrayList

class Camera2HelperFace(val mActivity: Activity, private val mTextureView: AutoFitTextureView) {
    companion object {
        const val PREVIEW_WIDTH = 1080                                        //预览的宽度
        const val PREVIEW_HEIGHT = 1440                                       //预览的高度
        const val SAVE_WIDTH = 720                                            //保存图片的宽度
        const val SAVE_HEIGHT = 1280                                          //保存图片的高度
    }

    private lateinit var mCameraManager:CameraManager
    private var mImageReader:ImageReader? = null
    private var mCameraDevice:CameraDevice? = null
    private var mCameraCaptureSession:CameraCaptureSession? = null

    private var mCameraId = "0"
    private lateinit var mCameraCharacteristics: CameraCharacteristics

    private var mCameraSensorOrientation = 0
    private var mCameraFacing = CameraCharacteristics.LENS_FACING_BACK
    private val mDisplayRotation = mActivity.windowManager.defaultDisplay.rotation
    private var mFaceDetectMode = CaptureResult.STATISTICS_FACE_DETECT_MODE_OFF

    private var canTakePic = true
    private var canExchangeCamera = false
    private var openFaceDetect = true
    private var mFaceDetectMatrix = Matrix()
    private var mFacesRect = ArrayList<RectF>()
    private var mFaceDetectListener: FaceDetectListener? = null

    private var mCameraHandler: Handler
    private val handlerThread = HandlerThread("CameraThread")

    private var mPreviewSize = Size(PREVIEW_WIDTH, PREVIEW_HEIGHT)
    private var mSavePicSize = Size(SAVE_WIDTH, SAVE_HEIGHT)

    interface FaceDetectListener {
        fun onFaceDetect(faces: Array<Face>, facesRect: ArrayList<RectF>)
    }
    fun setFaceDetectListener(listener: FaceDetectListener) {
        this.mFaceDetectListener = listener
    }

    init {
        handlerThread.start()
        mCameraHandler = Handler(handlerThread.looper)
        mTextureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                configureTransform(width,height)
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                releaseCamera()
                return true
            }

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                configureTransform(width,height)
                initCameraInfo()
            }

        }
    }
    private fun initCameraInfo() {
        mCameraManager = mActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = mCameraManager.cameraIdList
        if (cameraIdList.isEmpty()){
            mActivity.toast("没有可用相机")
            return
        }
        cameraIdList.forEach {
            val cameraCharacteristics =
                mCameraManager.getCameraCharacteristics(it)
            val facing = cameraCharacteristics[CameraCharacteristics.LENS_FACING]
            if (facing == mCameraFacing){
                mCameraId = it
                mCameraCharacteristics = cameraCharacteristics
            }
            log("设备中的摄像头 $it")
        }
        val supportLevel =
            mCameraCharacteristics[CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL]
        if (supportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY){
            mActivity.toast("相机硬件不支持新特性")
        }
        mCameraSensorOrientation =
            mCameraCharacteristics[CameraCharacteristics.SENSOR_ORIENTATION]!!

        val configurationMap =
            mCameraCharacteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]

        val savePicSize =
            configurationMap?.getOutputSizes(ImageFormat.JPEG)
        val previewSize =
            configurationMap?.getOutputSizes(SurfaceTexture::class.java)

        val exchange = exchangeWidthAndHeight(mDisplayRotation, mCameraSensorOrientation)
        mSavePicSize = getBestSize(
            if (exchange) mSavePicSize.height else mSavePicSize.width,
            if (exchange) mSavePicSize.width else mSavePicSize.height,
            if (exchange) mSavePicSize.height else mSavePicSize.width,
            if (exchange) mSavePicSize.width else mSavePicSize.height,
            savePicSize?.toList()?: emptyList()
        )
        mPreviewSize = getBestSize(
            if (exchange) mPreviewSize.height else mPreviewSize.width,
            if (exchange) mPreviewSize.width else mPreviewSize.height,
            if (exchange) mTextureView.height else mTextureView.width,
            if (exchange) mTextureView.width else mTextureView.height,
            previewSize?.toList()?: emptyList()
        )
        mTextureView.surfaceTexture?.setDefaultBufferSize(mPreviewSize.width,mPreviewSize.height)
        log("预览最优尺寸 ：${mPreviewSize.width} * ${mPreviewSize.height}, 比例  " +
                "${mPreviewSize.width.toFloat() / mPreviewSize.height}")
        log("保存图片最优尺寸 ：${mSavePicSize.width} * ${mSavePicSize.height}, 比例  " +
                "${mSavePicSize.width.toFloat() / mSavePicSize.height}")
        val orientation = mActivity.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            mTextureView.setAspectRatio(mPreviewSize.width,mPreviewSize.height)
        }else{
            mTextureView.setAspectRatio(mPreviewSize.height,mPreviewSize.width)
        }
        mImageReader = ImageReader.newInstance(mPreviewSize.width,mPreviewSize.height,ImageFormat.JPEG,1)
        mImageReader?.setOnImageAvailableListener(onImageAvailableListener,mCameraHandler)
        if (openFaceDetect){
            initFaceDetect()
        }
        openCamera()
    }
    private val onImageAvailableListener =
        ImageReader.OnImageAvailableListener{
            val image = it.acquireNextImage()
            val byteBuffer = image.planes[0].buffer
            val byteArray = ByteArray(byteBuffer.remaining())
            byteBuffer[byteArray]
            image.close()

            BitmapUtils.savePic(byteArray,"camera2",
                mCameraSensorOrientation == 270,{savedPath, time ->
                    mActivity.runOnUiThread {
                        mActivity.toast("图片保存成功！ 保存路径：$savedPath 耗时：$time")
                    }
                },{msg ->
                    mActivity.runOnUiThread {
                        mActivity.toast("图片保存失败！ $msg")
                    }
                })
    }
    private fun initFaceDetect() {
        val faceDetectCount =
            mCameraCharacteristics[CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT]
        val faceDetectModes =
            mCameraCharacteristics[CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES]
        if (faceDetectModes == null){
            mActivity.toast("相机硬件不支持人脸检测")
            return
        }
        mFaceDetectMode = when{
            faceDetectModes.contains(CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL)->
                CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL
            faceDetectModes.contains(CaptureRequest.STATISTICS_FACE_DETECT_MODE_SIMPLE)->
                CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL
            else->CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF
        }
        if (mFaceDetectMode == CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF){
            mActivity.toast("相机硬件不支持人脸检测")
            return
        }

        val activeArraySizeRect =
            mCameraCharacteristics[CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE]!!
        val scaleWidth = mPreviewSize.width/activeArraySizeRect.width().toFloat()
        val scaleHeight = mPreviewSize.height/activeArraySizeRect.height().toFloat()
        val mirror = mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT

        mFaceDetectMatrix.setRotate(mCameraSensorOrientation.toFloat())
        mFaceDetectMatrix.postScale(if (mirror) -scaleWidth else scaleWidth,scaleHeight)
        if (exchangeWidthAndHeight(mDisplayRotation,mCameraSensorOrientation)){
            mFaceDetectMatrix.postTranslate(mPreviewSize.height.toFloat(),
                mPreviewSize.width.toFloat())
        }
        log("成像区域  ${activeArraySizeRect.width()}  ${activeArraySizeRect.height()} " +
                "比例: ${activeArraySizeRect.width().toFloat() / activeArraySizeRect.height()}")
        log("预览区域  ${mPreviewSize.width}  ${mPreviewSize.height} " +
                "比例 ${mPreviewSize.width.toFloat() / mPreviewSize.height}")

        for (mode in faceDetectModes) {
            log("支持的人脸检测模式 $mode")
        }
        log("同时检测到人脸的数量 $faceDetectCount")
    }
    @SuppressLint("MissingPermission")
    private fun openCamera() {
        mCameraManager.openCamera(mCameraId,object:CameraDevice.StateCallback(){
            override fun onOpened(camera: CameraDevice) {
                log("onOpened")
                mCameraDevice = camera
                createCaptureSession(camera)
            }

            override fun onDisconnected(camera: CameraDevice) {
                log("onDisconnected")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                log("onError $error")
                mActivity.toast("打开相机失败！$error")
            }

        },mCameraHandler)
    }
    private fun createCaptureSession(cameraDevice: CameraDevice) {
        val captureRequestBuilder =
            cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        val surface = Surface(mTextureView.surfaceTexture)
        captureRequestBuilder.addTarget(surface)
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        if (openFaceDetect && mFaceDetectMode!=CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF){
            captureRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,
                CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE)
        }

        cameraDevice.createCaptureSession(arrayListOf(surface,mImageReader?.surface),
            object:CameraCaptureSession.StateCallback(){
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    mActivity.toast("开启预览会话失败！")
                }

                override fun onConfigured(session: CameraCaptureSession) {
                    mCameraCaptureSession = session
                    session.setRepeatingRequest(captureRequestBuilder.build(),mCaptureCallBack,mCameraHandler)
                }

            },mCameraHandler)
    }

    private val mCaptureCallBack =
        object:CameraCaptureSession.CaptureCallback(){
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                super.onCaptureCompleted(session, request, result)
                if (openFaceDetect && mFaceDetectMode!=CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF)
                    handleFaces(result)
                canExchangeCamera = true
                canTakePic = true
            }

            override fun onCaptureFailed(
                session: CameraCaptureSession,
                request: CaptureRequest,
                failure: CaptureFailure
            ) {
                super.onCaptureFailed(session, request, failure)
                log("onCaptureFailed")
                mActivity.toast("开启预览失败！")
            }
    }

    private fun handleFaces(result: TotalCaptureResult) {
        val faces = result[CaptureResult.STATISTICS_FACES]
        mFacesRect.clear()
        if (faces!=null){
            for (face in faces){
                val bounds = face.bounds
                val left = bounds.left
                val top = bounds.top
                val right = bounds.right
                val bottom = bounds.bottom

                val rawFaceRect = RectF(left.toFloat(),top.toFloat(),right.toFloat(),bottom.toFloat())
                mFaceDetectMatrix.mapRect(rawFaceRect)

                val resultFaceRect = if (mCameraFacing == CaptureRequest.LENS_FACING_FRONT){
                    rawFaceRect
                }else{
                    RectF(rawFaceRect.left,rawFaceRect.top-mPreviewSize.width,rawFaceRect.right,
                        rawFaceRect.bottom-mPreviewSize.width)
                }
                mFacesRect.add(resultFaceRect)
                log("原始人脸位置: ${bounds.width()} * ${bounds.height()}   ${bounds.left} " +
                        "${bounds.top} ${bounds.right} ${bounds.bottom}   分数: ${face.score}")
                log("转换后人脸位置: ${resultFaceRect.width()} * ${resultFaceRect.height()}  " +
                        " ${resultFaceRect.left} ${resultFaceRect.top} " +
                        "${resultFaceRect.right} ${resultFaceRect.bottom}   分数: ${face.score}")
            }
        }
        mActivity.runOnUiThread {
            if (faces!=null){
                mFaceDetectListener?.onFaceDetect(faces,mFacesRect)
            }
        }
        log("onCaptureCompleted  检测到 ${faces?.size} 张人脸")
    }
    fun takePic() {
        if (mCameraDevice == null || !mTextureView.isAvailable || !canTakePic) return
        mCameraDevice?.apply {
            val captureRequestBuilder =
                createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureRequestBuilder.addTarget(mImageReader!!.surface)
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mCameraSensorOrientation)
            mCameraCaptureSession?.capture(captureRequestBuilder.build(), null, mCameraHandler)
                ?: mActivity.toast("拍照异常！")
        }
    }
    fun exchangeCamera() {
        if (mCameraDevice == null || !canExchangeCamera || !mTextureView.isAvailable) return

        mCameraFacing = if (mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT)
            CameraCharacteristics.LENS_FACING_BACK
        else
            CameraCharacteristics.LENS_FACING_FRONT

        mPreviewSize = Size(PREVIEW_WIDTH, PREVIEW_HEIGHT)
        releaseCamera()
        initCameraInfo()
    }
    private fun getBestSize(targetWidth: Int, targetHeight: Int,
                            maxWidth: Int, maxHeight: Int, sizeList: List<Size>): Size {
        val bigEnough = ArrayList<Size>()
        val notBigEnough = ArrayList<Size>()

        sizeList.forEach {
            if (it.width<=maxWidth && it.height<=maxHeight
                && it.width == it.height*targetWidth/targetHeight){
                if (it.width>=targetWidth && it.height>=targetHeight){
                    bigEnough.add(it)
                }else{
                    notBigEnough.add(it)
                }
            }
            log("系统支持的尺寸: ${it.width} * ${it.height} ,  " +
                    "比例 ：${it.width.toFloat() / it.height}")
        }
        log("最大尺寸 ：$maxWidth * $maxHeight, 比例 ：${targetWidth.toFloat() / targetHeight}")
        log("目标尺寸 ：$targetWidth * $targetHeight, 比例 ：${targetWidth.toFloat() / targetHeight}")

        return when{
            bigEnough.size>0 -> Collections.min(bigEnough,CompareSizesByArea())
            notBigEnough.size>0 -> Collections.max(notBigEnough,CompareSizesByArea())
            else -> sizeList[0]
        }
    }
    private fun exchangeWidthAndHeight(displayRotation: Int, sensorOrientation: Int): Boolean {
        var exchange = false
        when(displayRotation){
            Surface.ROTATION_0,Surface.ROTATION_180->
                if (sensorOrientation == 90 || sensorOrientation == 270){
                    exchange = true
                }
            Surface.ROTATION_90,Surface.ROTATION_270->
                if (sensorOrientation==0 || sensorOrientation==180){
                    exchange = true
                }
            else ->log("Display rotation is invalid: $displayRotation")
        }
        log("屏幕方向  $displayRotation")
        log("相机方向  $sensorOrientation")
        return exchange
    }

    fun releaseCamera() {
        mCameraCaptureSession?.close()
        mCameraCaptureSession = null

        mCameraDevice?.close()
        mCameraDevice = null

        mImageReader?.close()
        mImageReader = null

        canExchangeCamera = false
    }

    fun releaseThread() {
        handlerThread.quitSafely()
        handlerThread.join()
    }

    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        val rotation = mActivity.windowManager.defaultDisplay.rotation
        val matrix  = Matrix()
        val viewRect = RectF(0f,0f,viewWidth.toFloat(),viewHeight.toFloat())
        val bufferRect = RectF(0f,0f,mPreviewSize.height.toFloat(),mPreviewSize.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation){
            bufferRect.offset(centerX-bufferRect.centerX(),centerY-bufferRect.centerY())
            matrix.setRectToRect(viewRect,bufferRect,Matrix.ScaleToFit.FILL)
            val scale = Math.max(viewHeight.toFloat()/mPreviewSize.height,
                viewWidth.toFloat()/mPreviewSize.width)
            matrix.postScale(scale,scale,centerX,centerY)
            matrix.postRotate((90*(rotation-1)).toFloat(),centerX,centerY)
        }else if (Surface.ROTATION_180 == rotation){
            matrix.postRotate(180f,centerX,centerY)
        }
        mTextureView.setTransform(matrix)
        log("configureTransform $viewWidth  $viewHeight")
    }
    private class CompareSizesByArea : Comparator<Size> {
        override fun compare(size1: Size, size2: Size): Int {
            return java.lang.Long.signum(size1.width.toLong() * size1.height - size2.width.toLong() * size2.height)
        }
    }

}