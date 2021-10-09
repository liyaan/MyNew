package com.liyaan.camera.camera2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Camera
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.core.content.ContextCompat
import com.liyaan.camera.util.BitmapUtils
import com.liyaan.camera.util.log
import com.liyaan.camera.util.toast
import java.util.*
import kotlin.collections.ArrayList

class Camera2Helper(val mActivity: Activity, private val mTextureView: TextureView){
    companion object {
        const val PREVIEW_WIDTH = 720                                         //预览的宽度
        const val PREVIEW_HEIGHT = 1280                                       //预览的高度
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

    private var canTakePic = true
    private var canExchangeCamera = false

    private var mCameraHandler:Handler
    private val handlerThread = HandlerThread("CameraThread")

    private var mPreviewSize = Size(PREVIEW_WIDTH, PREVIEW_HEIGHT)
    private var mSavePicSize = Size(SAVE_WIDTH, SAVE_HEIGHT)

    init {
        handlerThread.start()
        mCameraHandler = Handler(handlerThread.looper)

        mTextureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

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
                initCameraInfo()
            }

        }
    }
    private fun initCameraInfo(){
        mCameraManager = mActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraList = mCameraManager.cameraIdList
        if (cameraList.isEmpty()){
            mActivity.toast("没有可用相机")
            return
        }

        for (id in cameraList){
            val cameraCharacteristics =
                mCameraManager.getCameraCharacteristics(id)
            val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)

            if (facing == mCameraFacing){
                mCameraId = id
                mCameraCharacteristics = cameraCharacteristics
            }
            log("设备中的摄像头 $id")
        }
        val supportLevel = mCameraCharacteristics[CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL]
        if (supportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY){

        }
        mCameraSensorOrientation = mCameraCharacteristics[CameraCharacteristics.SENSOR_ORIENTATION]!!
        val configurationMap =
            mCameraCharacteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]
        val savePicSize = configurationMap?.getOutputSizes(ImageFormat.JPEG)
        val previewSize = configurationMap?.getOutputSizes(SurfaceTexture::class.java)
        val exchange = exchangeWidthAndHeight(mDisplayRotation, mCameraSensorOrientation)
        mSavePicSize = getBestSize(
            if (exchange)mSavePicSize.height else mSavePicSize.width,
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
            previewSize?.toList() ?: emptyList()
        )
        mTextureView.surfaceTexture?.setDefaultBufferSize(mPreviewSize.width,mPreviewSize.height)
        mImageReader = ImageReader.newInstance(mSavePicSize.width,mSavePicSize.height,ImageFormat.JPEG,1)
        mImageReader?.setOnImageAvailableListener(onImageAvailableListener,mCameraHandler)

        openCamera()
    }
    private val onImageAvailableListener = ImageReader.OnImageAvailableListener {
        val image = it.acquireNextImage()
        val byteBuffer = image.planes[0].buffer
        val byteArray = ByteArray(byteBuffer.remaining())
        byteBuffer.get(byteArray)
        image.close()
        BitmapUtils.savePic(byteArray,"camear2",
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

    private fun openCamera(){
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
            !=PackageManager.PERMISSION_GRANTED){
            mActivity.toast("没有相机权限！")
            return
        }
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
    private fun createCaptureSession(cameraDevice: CameraDevice){
        val captureRequestBuilder =
            cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        val surface = Surface(mTextureView.surfaceTexture)
        captureRequestBuilder.addTarget(surface)
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        cameraDevice.createCaptureSession(arrayListOf(surface,mImageReader?.surface),
            object:CameraCaptureSession.StateCallback(){
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    mActivity.toast("开启预览会话失败！")
                }

                override fun onConfigured(session: CameraCaptureSession) {
                    mCameraCaptureSession = session
                    session.setRepeatingRequest(captureRequestBuilder.build(), mCaptureCallBack, mCameraHandler)
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
    fun takePic() {
        if (mCameraDevice == null || !mTextureView.isAvailable || !canTakePic) return
        mCameraDevice?.apply {
            val captureRequestBuilder =
                createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureRequestBuilder.addTarget(mImageReader!!.surface)

            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
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
        mCameraFacing = if (mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT){
            CameraCharacteristics.LENS_FACING_BACK
        }else{
            CameraCharacteristics.LENS_FACING_FRONT
        }
        mPreviewSize = Size(PREVIEW_WIDTH, PREVIEW_HEIGHT)
        releaseCamera()
        initCameraInfo()
    }
    private fun getBestSize(targetWidth: Int, targetHeight: Int,
                            maxWidth: Int, maxHeight: Int, sizeList: List<Size>): Size {
        val bigEnough = ArrayList<Size>()
        val notBigEnough = ArrayList<Size>()
        for (size in sizeList){
            if (size.width<=maxWidth && size.height<=maxHeight
                && size.width == size.height*targetWidth/targetHeight){
                if (size.width>=targetWidth && size.height>=targetHeight){
                    bigEnough.add(size)
                }else{
                    notBigEnough.add(size)
                }
            }
        }

        return when{
            bigEnough.size>0 -> Collections.min(bigEnough, CompareSizesByArea())
            notBigEnough.size>0 -> Collections.max(notBigEnough,CompareSizesByArea())
            else -> sizeList[0]
        }
    }
    private fun exchangeWidthAndHeight(displayRotation: Int, sensorOrientation: Int): Boolean {
        var exchange = false
        when(displayRotation){
            Surface.ROTATION_0,Surface.ROTATION_180 ->
                if (sensorOrientation == 90 || sensorOrientation ==270){
                    exchange = true
                }
            Surface.ROTATION_90, Surface.ROTATION_270 ->
                if (sensorOrientation == 0 || sensorOrientation == 180) {
                    exchange = true
                }
            else -> log("Display rotation is invalid: $displayRotation")
        }
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
    }

    private class CompareSizesByArea : Comparator<Size> {
        override fun compare(size1: Size, size2: Size): Int {
            return java.lang.Long.signum(size1.width.toLong() * size1.height - size2.width.toLong() * size2.height)
        }
    }
}