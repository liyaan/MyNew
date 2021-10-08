package com.liyaan.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.liyaan.camera.util.DecodeImgTask
import com.liyaan.camera.util.FileUtil
import com.liyaan.statusbar.R
import kotlinx.android.synthetic.main.activity_capture.*
import java.io.File

class CaptureActivity : AppCompatActivity(){
    companion object {
        const val AUTHORITY = "com.liyaan.statusbar.fileProvider"

        const val REQUEST_CODE_CAPTURE_SMALL = 1
        const val REQUEST_CODE_CAPTURE_RAW = 2
        const val REQUEST_CODE_CAPTURE = 3
        const val REQUEST_CODE_CAPTURE_CROP = 4
        const val REQUEST_CODE_ALBUM = 5
        const val REQUEST_CODE_VIDEO = 6

        var imageFile: File? = null
        var imageCropFile: File? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)
        btnCaptureSmall.setOnClickListener { gotoCaptureSmall() }
        btnCaptureRaw.setOnClickListener { gotoCaptureRaw() }
        btnCaptureAndClip.setOnClickListener { gotoCaptureCrop() }
        btnAlbumAndClip.setOnClickListener { gotoGallery() }
        btnCaptureVideo.setOnClickListener { gotoCaptureVideo() }
    }

    private fun gotoCaptureVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (intent.resolveActivity(packageManager) != null)
            startActivityForResult(intent, REQUEST_CODE_VIDEO)
    }

    private fun gotoGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_ALBUM)
    }

    private fun gotoCaptureCrop() {
        imageFile = FileUtil.createImageFile()
        imageFile?.let {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val imgUri = FileProvider.getUriForFile(this, AUTHORITY,it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri)
            }else{
                intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(it))
            }
            intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString())
            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(intent, REQUEST_CODE_CAPTURE)
            }
        }
    }

    private fun gotoCaptureRaw() {
        imageFile = FileUtil.createImageFile()
        imageFile?.let {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val imgUri = FileProvider.getUriForFile(this, AUTHORITY, it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri)
            }else{
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(it))
            }
            intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString())
            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_RAW)
            }
        }
    }

    //拍照(返回缩略图)
    private fun gotoCaptureSmall() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.let {
            startActivityForResult(intent,REQUEST_CODE_CAPTURE_SMALL)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAPTURE_SMALL -> {
                    data?.data
                    val bitmap = data?.extras?.get("data") as Bitmap
                    ivResult.setImageBitmap(bitmap)
                }
                REQUEST_CODE_CAPTURE_RAW->{
                    imageFile?.let {
                        DecodeImgTask(ivResult).execute(it.absolutePath)
                    }
                }
                REQUEST_CODE_CAPTURE->{
                    imageFile?.let {
                        val sourceUri = FileProvider.getUriForFile(this, AUTHORITY,it)
                        gotoCrop(sourceUri)
                    }
                }
                REQUEST_CODE_CAPTURE_CROP->{
                    imageCropFile?.let {
                        ivResult.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                    }
                }
                REQUEST_CODE_ALBUM->{
                    data?.let {
                        gotoCrop(it.data!!)
                    }
                }
                REQUEST_CODE_VIDEO -> {   //录制视频成功后播放
                    data?.let {
                        val uri = it.data
                        videoView.visibility = View.VISIBLE
                        videoView.setVideoURI(uri)
                        videoView.start()
                        Log.d("tag", "视频uri $uri")
                    }
                }
            }
        }

    }
    //裁剪
    private fun gotoCrop(sourceUri: Uri) {
        imageCropFile = FileUtil.createImageFile(true)
        imageCropFile?.let {
            val intent = Intent("com.android.camera.action.CROP")
            intent.putExtra("crop", "true")
            intent.putExtra("aspectX", 1)    //X方向上的比例
            intent.putExtra("aspectY", 1)    //Y方向上的比例
            intent.putExtra("outputX", 500)  //裁剪区的宽
            intent.putExtra("outputY", 500)  //裁剪区的高
            intent.putExtra("scale ", true)  //是否保留比例
            intent.putExtra("return-data", false)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.setDataAndType(sourceUri, "image/*")

            // 7.0 使用 FileProvider 并赋予临时权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            }

            // 11.0无法访问私有域，所以这里要确保裁剪后的文件保存在公有目录中
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 这里要保证输出的文件是在公有目录
                // 由于此demo中默认保存的就是公有目录，所以这里不做任何操作，如果是自己的项目，请根据具体情况修改

            } else {
            }

            val outputUri = Uri.fromFile(it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)            //设置输出

            Log.d("tag", "输入 $sourceUri")
            Log.d("tag", "输出 $outputUri")
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_CROP)
        }
    }
}