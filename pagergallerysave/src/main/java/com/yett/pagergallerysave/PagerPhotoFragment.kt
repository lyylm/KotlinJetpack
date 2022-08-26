package com.yett.pagergallerysave

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.cell_pager_photo.view.*
import kotlinx.android.synthetic.main.fragment_pager_photo.*
import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
const val REQUEST_WRITE_EXTERNAL_STORAGE = 1
/**
 * A simple [Fragment] subclass.
 * Use the [PagerPhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PagerPhotoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pager_photo, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PagerPhotoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PagerPhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //获取导航传递的数据
        val photoList = arguments?.getParcelableArrayList<PhotoItem>(PHOTO_LIST)
        //初始化适配器
        PagerPhotoAdapter().apply {
            viewPager2Photo.adapter = this
            submitList(photoList)
        }
        //给底部的textView设置数据
        viewPager2Photo.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = getString(R.string.photo_tag,position+1,photoList?.size)
            }
        })
        //设置图片从点击的位置开始显示，不需要动画，所有为false
        viewPager2Photo.setCurrentItem(arguments?.getInt(PHOTO_POSITION)?:0,false)
        //设置为竖直方向滑动查看图片
        viewPager2Photo.orientation = ViewPager2.ORIENTATION_VERTICAL
        //给保存按钮添加事件；这是调用lambda表达式的方式
        imageViewSave.setOnClickListener {
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE
                )
            }else{
                //TODO 调用携程方法
                viewLifecycleOwner.lifecycleScope.launch{
                    savePhoto()
                }
            }
        }

        //如果询问权限，权限对话的操作如下

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_WRITE_EXTERNAL_STORAGE->{
                if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    viewLifecycleOwner.lifecycleScope.launch {
                        savePhoto()
                    }
                }else{
                    Toast.makeText(requireContext(),"保存失败",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //异步线程保存图片 关键字suspend，携程
    private suspend fun savePhoto(){
        //携程的作用域和携程的工作线程
        withContext(Dispatchers.IO){
            //获取PagerPhotoViewHolder
            val holder:PagerPhotoViewHolder =
                //先将viewPager2Photo转换为RecyclerView，获取适配器中当前的子项，并将其转换为holder
                (viewPager2Photo[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2Photo.currentItem) as PagerPhotoViewHolder
            //将子布局控件中的photoView上当前显示的图片转换为bitmap数据
            val bitmap:Bitmap = holder.itemView.photoViewCellPager.drawable.toBitmap()
            //添加延时
            //delay(5000)
            //先申请图片存放的空间位置
            val saveUri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            )?:kotlin.run {
                //在携程中要操作主线程的控件等，需要如下操作
                MainScope().launch { Toast.makeText(requireContext(),"保存失败",Toast.LENGTH_SHORT).show() }
                return@withContext//返回到当前的携程作用域
            }
            //保存图片
            requireContext().contentResolver.openOutputStream(saveUri).use {
                //将bitmap进行压缩保存为JPEG格式，压缩百分之九十
                if (bitmap.compress(Bitmap.CompressFormat.JPEG,90,it)){
                    MainScope().launch { Toast.makeText(requireContext(),"保存成功",Toast.LENGTH_SHORT).show() }
                }else{
                    MainScope().launch{Toast.makeText(requireContext(),"保存失败",Toast.LENGTH_SHORT).show()}
                }
            }
        }
    }
}