package com.example.myapplication

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import com.google.android.material.internal.ContextUtils.getActivity
import java.util.*
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.recyclerview.widget.RecyclerView
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var dataList: LinkedList<String> = LinkedList()
    private lateinit var hb: HorizontalItemBar;
    private var data: MutableList<String> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hb = findViewById(R.id.HB)
        hb.setFocusable(true);
        hb.setIndicatorStyle(HorizontalItemBar.Style.INDICATOR2)


        val dm = DisplayMetrics()
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.e("=========dm.density", "${dm.density}")
        hb.setMoveListener { index, name, keyCode ->


            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                Log.e("---------------", "左键$index==$name")
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                Log.e("---------------", "上键$index==$name")

            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                Log.e("---------------", "右键$index==$name")
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                Log.e("---------------", "下键$index==$name")
            }
            if (keyCode == -1) {
                Log.e("---------------", "下键$index==$name")
            }
        }

        hb.setOnConfirmListener(object : HorizontalItemBar.OnConfirmListener {
            override fun click(index: Int, name: String?) {
                Log.e(
                    "---------------", "确认键：$index==$name"
                )
            }
        })

        dataList.add("测试1")
        dataList.add("测试2")
        dataList.add("测试3")
        dataList.add("测试4")
        dataList.add("测试5")
        dataList.add("测试6")
        hb.setData(dataList, false)

    }
}