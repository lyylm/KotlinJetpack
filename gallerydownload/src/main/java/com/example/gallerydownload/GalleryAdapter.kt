package com.example.gallerydownload

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.cell_gallery.view.*

const val PHOTO_LIST = "photoList"
const val PHOTO_POSITION = "photoPosition"
class GalleryAdapter:ListAdapter<PhotoItem,GalleryViewHolder>(DiffCallbck) {
    //这是一个比较器
    object DiffCallbck:DiffUtil.ItemCallback<PhotoItem>(){
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val holder = GalleryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_gallery,parent,false))
        //给单元项目添加点击事件
        holder.itemView.setOnClickListener {
            Bundle().apply {
                //传递所有的图片列表
                putParcelableArrayList(PHOTO_LIST,ArrayList(currentList))
                //传递当前点击图片的position
                putInt(PHOTO_POSITION,holder.adapterPosition)
                //进行导航
                holder.itemView.findNavController().navigate(R.id.action_galleryFragment_to_pagerPhotoFragment,this)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        //获取当前项图片的信息
        val photoItem:PhotoItem = getItem(position)
        //对cell_gallery上的控件进行设置
        with(holder.itemView){
            //设置图片占位符在等待图片加载的时候，波纹闪烁的动态
            shimmerLayoutCellGallery.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            //对textView控件进行设置
            textViewUser.text=photoItem.photoUser
            textViewLikes.text = photoItem.photoLikes.toString()
            textViewComments.text = photoItem.photoComments.toString()
            //获取图片控件的高度
            imageViewCellGallery.layoutParams.height = photoItem.photoHeight
        }
        //设置图片占位符在等待图片加载的时候，波纹闪烁的动态
//        holder.itemView.shimmerLayoutCellGallery.apply {
//            setShimmerColor(0x55FFFFFF)
//            setShimmerAngle(0)
//            startShimmerAnimation()
//        }
        //获取图片的高度
        //holder.itemView.imageViewCellGallery.layoutParams.height = getItem(position).photoHeight
        //glide为当前的iamgeView加载图片资源
        Glide.with(holder.itemView)
            .load(getItem(position).previewUrl)//设置当前位置的图片网络资源路径
            .placeholder(R.drawable.photo_placeholder)//设置占位图片
            .listener(object : RequestListener<Drawable>{
                //图片加载失败监听
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
                //图片加载成功监听
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    //加载成功后占位图片停止闪烁
                    return false.also { holder.itemView.shimmerLayoutCellGallery.stopShimmerAnimation() }
                }
            })
            .into(holder.itemView.imageViewCellGallery)//设置图片加载的控件
    }
}

class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)