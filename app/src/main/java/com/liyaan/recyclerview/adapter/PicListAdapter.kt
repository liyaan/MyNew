package com.liyaan.recyclerview.adapter

import android.R.attr.fragment
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.liyaan.mynew.R
import com.liyaan.recyclerview.bean.Meizhi
import kotlinx.android.synthetic.main.item_meizhi.view.*
import java.util.*


class PicListAdapter(private val context: Context)
    :ListAdapter<Meizhi,PicListAdapter.MeizhiHolder>(PicListItemCallBack()) {
    val random = Random()
    private val handler: Handler = Handler()
    class MeizhiHolder(item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeizhiHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_meizhi,parent,false)
        return MeizhiHolder(view)
    }

    override fun onBindViewHolder(holder: MeizhiHolder, position: Int) {
        val entity = getItem(position)
        entity.url?.let{
            Glide.with(context).asDrawable().listener(
                object:RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    handler.post {
                        Glide.with(context).asDrawable()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .load(IMG_LIST_URLS[(IMG_LIST_URLS.indices).random()])
                            .into(holder.itemView.ivPic)
                    }
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.ivPic.setImageDrawable(resource)
                    return true
                }

            })
                .diskCacheStrategy(DiskCacheStrategy.ALL).load(it).into(holder.itemView.ivPic)
        }
    }
}
private  val IMG_LIST_URLS =
    arrayOf("https://img1.baidu.com/it/u=271996978,91666721&fm=26&fmt=auto",
        "https://img0.baidu.com/it/u=3083490177,4087830236&fm=26&fmt=auto",
        "https://img2.baidu.com/it/u=1910691410,738959961&fm=26&fmt=auto",
        "https://img0.baidu.com/it/u=2897994812,3997461950&fm=26&fmt=auto",
        "https://img2.baidu.com/it/u=1443687308,2680039915&fm=26&fmt=auto",
        "https://img2.baidu.com/it/u=2747573269,888746930&fm=26&fmt=auto",
        "https://img1.baidu.com/it/u=3919770115,4269125285&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=750",
        "https://img1.baidu.com/it/u=838969580,114622876&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=750",
        "https://img1.baidu.com/it/u=1785717022,2470862482&fm=26&fmt=auto",
        "https://img1.baidu.com/it/u=335735232,2913383325&fm=26&fmt=auto",
        "https://img0.baidu.com/it/u=936606691,2979406002&fm=26&fmt=auto",
        "https://img1.baidu.com/it/u=2340275591,3916371837&fm=224&fmt=auto&gp=0.jpg",
        "https://img1.baidu.com/it/u=79603525,2490481877&fm=26&fmt=auto",
        "https://img0.baidu.com/it/u=4147408133,4029755281&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=1084",
        "https://img1.baidu.com/it/u=3621730896,2151036939&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=750")
class PicListItemCallBack:DiffUtil.ItemCallback<Meizhi>(){
    override fun areItemsTheSame(oldItem: Meizhi, newItem: Meizhi): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: Meizhi, newItem: Meizhi): Boolean {
        return oldItem == newItem
    }

}