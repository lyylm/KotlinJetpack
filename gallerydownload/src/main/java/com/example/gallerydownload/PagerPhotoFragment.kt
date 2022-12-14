package com.example.gallerydownload

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.cell_gallery.*
import kotlinx.android.synthetic.main.cell_pager_photo.view.*
import kotlinx.android.synthetic.main.fragment_pager_photo.*
import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
const val REQUEST_WRITE_EXTERNAL_STORAGE = 1
const val TAG = "lyy"

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
        //?????????gallery???????????????????????????????????????
        val photoList = arguments?.getParcelableArrayList<PhotoItem>(PHOTO_LIST)
        PagerPhotoAdapter().apply {
            viewPager2.adapter = this
            submitList(photoList)
        }

        //??????????????????textView???????????????????????????
        //???ViewPager2??????????????????????????????
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {//???????????????????????????
                super.onPageSelected(position)
                //photoTag.text = "${position+1}/${photoList?.size}"
                //?????????????????????<string name="photo_tag">%d / %d</string>
                photoTag.text = getString(R.string.photo_tag,position+1,photoList?.size)
            }
        })
        //???ViewPager2???????????????????????????????????????
        viewPager2.setCurrentItem(arguments?.getInt(PHOTO_POSITION)?:0,false)

        //????????????????????????????????????
//        imageViewSave.setOnClickListener {
//            if (Build.VERSION.SDK_INT<29 && ContextCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
//                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                REQUEST_WRITE_EXTERNAL_STORAGE)
//            }else{
//                savePhotoOnMain()
//            }
//        }

        //??????????????????
        imageViewSave.setOnClickListener {
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE
                )
            } else {
                //TODO ??????????????????????????????????????????
                viewLifecycleOwner.lifecycleScope.launch {
                    savePhotoOnIO()
                }
            }
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when(requestCode){
//            REQUEST_WRITE_EXTERNAL_STORAGE->{
//                if (grantResults.isEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    savePhotoOnMain()
//                }else{
//                    Toast.makeText(requireContext(),"????????????",Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    //?????????????????????
    private fun savePhotoOnMain() {
        val holder:PagerPhotoViewHolder = (viewPager2[0] as RecyclerView)
            .findViewHolderForAdapterPosition(viewPager2.currentItem) as PagerPhotoViewHolder
        val bitmap:Bitmap = holder.itemView.imageView.drawable.toBitmap()
        //??????????????????????????????
//        if (MediaStore.Images.Media.insertImage(requireContext().contentResolver,bitmap,"","")==null){
//            Toast.makeText(requireContext(),"????????????",Toast.LENGTH_SHORT).show()
//        }else{
//            Toast.makeText(requireContext(),"????????????",Toast.LENGTH_SHORT).show()
//        }
        //?????????????????????
        val saveUri = (requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues()
        )?:kotlin.run {
            Toast.makeText(requireContext(),"????????????",Toast.LENGTH_SHORT).show()
            return
        })

        requireContext().contentResolver.openOutputStream(saveUri).use {
            if (bitmap.compress(Bitmap.CompressFormat.JPEG,90,it)){
                Toast.makeText(requireContext(),"????????????",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"????????????",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun savePhotoOnIO(){
        withContext(Dispatchers.IO){
            val holder:PagerPhotoViewHolder =
                (viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2.currentItem) as PagerPhotoViewHolder

            val bitmap:Bitmap = holder.itemView.imageView.drawable.toBitmap()
            //????????????????????????????????????????????????
            delay(5000)
            val saveUri= requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            )?:kotlin.run {
                MainScope().launch { Toast.makeText(requireContext(),"????????????",Toast.LENGTH_SHORT).show() }
                return@withContext
            }

            requireContext().contentResolver.openOutputStream(saveUri).use {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG,90,it)){
                    MainScope().launch { Toast.makeText(requireContext(),"????????????",Toast.LENGTH_SHORT).show() }
                }else{
                    MainScope().launch { Toast.makeText(requireContext(),"????????????",Toast.LENGTH_SHORT).show() }
                }
            }
        }
    }
}


























