package com.liyaan.onedownload

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.liyaan.mynew.R
import java.io.File

const val FAST_CLICK_DELAY_TIME:Long = 2000

class UpdateVersionShowDialog: DialogFragment() {
    private var downloadBean: DownloadBean? = null
    private var title: TextView? = null
    private  var content:TextView? = null
    private  var update:TextView? = null
    private  var lastClickTime: Long = 0
    companion object{
        @JvmStatic
        fun show(fragmentActivity: FragmentActivity, downloadBean: DownloadBean?) {
            val bundle = Bundle()
            bundle.putSerializable("download_bean", downloadBean)
            val dialog = UpdateVersionShowDialog()
            dialog.arguments = bundle
            dialog.show(fragmentActivity.supportFragmentManager, "aaaa")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments;
        downloadBean = bundle?.getSerializable("download_bean") as DownloadBean?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_fragment,container,false)
        initView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initEvent()
    }

    private fun initEvent() {
        update?.setOnClickListener {
            if (!isFastClick()){
                val targetFile = File(activity?.cacheDir,"update.apk")
                AppUpdater.getInstance().getNetManager().download(downloadBean!!.url!!,targetFile,object:INetDownloadCallBack{
                    override fun success(apkFile: File?) {
                        dismiss()
                        AppUtils.installApk(activity,apkFile?.path)
                    }

                    override fun failed(throwable: Throwable?) {
                        throwable?.printStackTrace();
                    }

                    override fun progress(progress: String?) {
                        Log.e("aaaaa", "progress ::$progress")
                        activity!!.runOnUiThread { update!!.text = progress }
                    }

                },UpdateVersionShowDialog::class)
            }

        }
    }

    private fun initView(view: View?) {
        title = view?.findViewById(R.id.title);
        content = view?.findViewById(R.id.content);
        update = view?.findViewById(R.id.update);
        title?.setText(downloadBean?.title);
        content?.setText(downloadBean?.content);
    }
    fun isFastClick(): Boolean {
        var flag = true
        val currentClickTime = System.currentTimeMillis()
        if (currentClickTime - lastClickTime >= FAST_CLICK_DELAY_TIME) {
            flag = false
        }
        lastClickTime = currentClickTime
        return flag
    }
}