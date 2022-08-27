package com.example.galleryupdate

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
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
import kotlinx.android.synthetic.main.gallery_footer.view.*

const val PHOTO_LIST = "photoList"
const val PHOTO_POSITION = "photoPosition"
class GalleryAdapter(val galleryViewModel: GalleryViewModel):ListAdapter<PhotoItem,GalleryViewHolder>(DiffCallback) {
    companion object{
        const val NORMAL_VIEW_TYPE = 0
        const val FOOTER_VIEW_TYPE = 1
    }

    var footerViewStatus = DATA_STATUS_CAN_LOAD_MORE

    object DiffCallback: DiffUtil.ItemCallback<PhotoItem>(){
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val holder:GalleryViewHolder
        if (viewType == NORMAL_VIEW_TYPE){
            holder = GalleryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_gallery, parent,false))

            holder.itemView.setOnClickListener {
                Bundle().apply {
                    putParcelableArrayList(PHOTO_LIST, ArrayList(currentList))
                    putInt(PHOTO_POSITION,holder.adapterPosition)
                    holder.itemView.findNavController().navigate(R.id.action_galleryFragment_to_pagerPhotoFragment,this)
                }
            }
        }else{
            LayoutInflater.from(parent.context).inflate(
                R.layout.gallery_footer,
                parent,
                false
            ).apply {
                holder = GalleryViewHolder(this)
                (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan=true
                //添加点击事件，当出现网络错误的时候，需要点击才能继续刷新数据

                setOnClickListener {
                    progressBar.visibility = View.VISIBLE
                    textViewFooter.text = "正在加载"
                    galleryViewModel.fetchData()
                }
            }

        }
        return holder
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        if (position == itemCount-1){
            with(holder.itemView){
                when(footerViewStatus){
                    DATA_STATUS_CAN_LOAD_MORE -> {
                        progressBar.visibility = View.VISIBLE
                        textViewFooter.text = "正在加载"
                        isClickable=false
                    }
                    DATA_STATUS_NO_MORE -> {
                        progressBar.visibility = View.GONE
                        textViewFooter.text = "全部加载完毕"
                        isClickable=false
                    }
                    DATA_STATUS_NETWORK_ERROR -> {
                        progressBar.visibility = View.GONE
                        textViewFooter.text = "网络故障，点击重试"
                        isClickable=true
                    }
                }
            }
            return
        }
        with(holder.itemView){
            shimmerLayoutCellGallery.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            val photoItem = getItem(position)
            textViewUser.text = photoItem.photoUser
            textViewLikes.text = photoItem.photoLikes.toString()
            textViewComments.text = photoItem.photoComments.toString()

            imageViewCellGallery.layoutParams.height = photoItem.photoHeight
        }

        Glide.with(holder.itemView)
            .load(getItem(position).previewUrl)
            .placeholder(R.drawable.photo_placeholder)
            .listener(object : RequestListener<Drawable>{
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
                    return false.also { holder.itemView.shimmerLayoutCellGallery.stopShimmerAnimation() }
                }
            })
            .into(holder.itemView.imageViewCellGallery)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()+1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount-1) FOOTER_VIEW_TYPE else NORMAL_VIEW_TYPE
    }
}

class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)