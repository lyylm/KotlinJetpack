package com.example.viewpagedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
//去掉导航栏，在AndroidManifest中设置android:theme="@style/Theme.Design.NoActionBar"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager2.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return 3
            }

            override fun createFragment(position: Int): Fragment {
                return when(position){
                    0->ScaleFragment()
                    1->RotateFragment()
                    else->TranslateFragment()
                }
            }
        }

        TabLayoutMediator(tabLayout,viewPager2){tab,position ->
            when(position){
                0->tab.text = "缩放"
                1->tab.text = "旋转"
                else->tab.text = "移动"
            }
        }.attach()
    }

}