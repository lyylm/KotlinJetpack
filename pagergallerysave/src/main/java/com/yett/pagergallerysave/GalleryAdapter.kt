package com.yett.pagergallerysave

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.cell_gallery.view.*

const val PHOTO_LIST = "photoList"
const val PHOTO_POSITION = "photoPosition"
class GalleryAdapter:ListAdapter<PhotoItem,GalleryViewHolder>(DiffCallback) {
    companion object{
        const val NORMAL_VIEW_TYPE = 0
        const val FOOTER_VIRE_TYPE = 1
    }

    object DiffCallback:DiffUtil.ItemCallback<PhotoItem>(){
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val holder:GalleryViewHolder
        if (viewType== NORMAL_VIEW_TYPE) {
            LayoutInflater.from(parent.context).inflate(
                R.layout.cell_gallery,
                parent,
                false).apply { holder= GalleryViewHolder(this) }
            //给子页面添加点击事件
            holder.itemView.setOnClickListener {
                Bundle().apply {
                    //currentList是当前适配器的所有数据
                    putParcelableArrayList(PHOTO_LIST, ArrayList(currentList))
                    putInt(PHOTO_POSITION, holder.adapterPosition)//点击图片的位置序号
                    //进行导航
                    holder.itemView.findNavController()
                        .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, this)
                }
            }
        }else{
            LayoutInflater.from(parent.context).inflate(
                R.layout.gallery_footer,
                parent,
                false
            ).apply { holder = GalleryViewHolder(this) }
                .also { (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan=true }
        }
        return holder
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        if (position == itemCount-1){
            return
        }
        //获取子页面中当前项的数据
        val photoItem:PhotoItem = getItem(position)
        with(holder.itemView){
            //启用当前项的闪烁功能
            shimmerLayoutCellGallery.apply {
                setShimmerColor(0x55FFFFFF)//闪烁的颜色及其透明度
                setShimmerAngle(0)
                startShimmerAnimation()//启动闪烁功能
            }
            //textView的内容填充
            textViewUser.text = photoItem.photoUser
            textViewLikes.text = photoItem.photoLikes.toString()
            textViewComments.text = photoItem.photoComments.toString()
            //根据当前项的图片高度设置当前子项的高度，防止查看图片，切换界面时出现图片布局调整的情况
            imageViewCellGallery.layoutParams.height = photoItem.photoHeight
        }

        //使用Glide给子项添加图片
        Glide.with(holder.itemView)
            .load(photoItem.previewUrl)
            .placeholder(R.drawable.photo_placeholder)
            .listener(object :RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    //加载成功后，停止闪烁
                    return false.also { holder.itemView.shimmerLayoutCellGallery?.stopShimmerAnimation() }
                }
            })
            .into(holder.itemView.imageViewCellGallery)

        //下拉到底部进行数据刷新加载

    }

    override fun getItemCount(): Int {
        return super.getItemCount()+1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount-1) FOOTER_VIRE_TYPE else NORMAL_VIEW_TYPE
    }
}
class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

























