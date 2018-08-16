package com.ppjun.android.vivonex

import android.animation.ValueAnimator
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mNavigateIcon: Menu2CloseDrawable
    lateinit var mValueAnimator: ValueAnimator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolBar)
//        mNavigateIcon = Menu2CloseDrawable()
//        mNavigateIcon.setDP(this, 18.0f)
//        mNavigateIcon.setColor(Color.WHITE)
//        toolBar.navigationIcon = mNavigateIcon
//        toolBar.setTitleTextColor(Color.WHITE)
//        toolBar.setOnClickListener {
//
//        }
        val list = ArrayList<PPTimePickerVo>()

        for (i in 0 until 24){


            var index:String = ""

            if(i%2==0){
               // list.add(PPTimePickerVo(1, false, true, index))
            }


        }
        list.add(PPTimePickerVo(1, true, true, "9"))
        list.add(PPTimePickerVo(1, true, true, "9.5"))
        list.add(PPTimePickerVo(1, true, true, "10"))
        list.add(PPTimePickerVo(1, true, true, "10.5"))
        list.add(PPTimePickerVo(1, false, true, "11"))
        list.add(PPTimePickerVo(1, false, true, "11.5"))
        list.add(PPTimePickerVo(1, false, true, "12"))
        list.add(PPTimePickerVo(1, false, true, "12.5"))
        list.add(PPTimePickerVo(1, false, true, "13"))
        list.add(PPTimePickerVo(1, false, true, "13.5"))
        list.add(PPTimePickerVo(1, false, true, "14"))
        list.add(PPTimePickerVo(1, false, true, "14.5"))
        mPPTimePicker.setTimePickerVos(list)






    }


}
