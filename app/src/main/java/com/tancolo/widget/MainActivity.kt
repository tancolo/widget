package com.tancolo.widget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.tancolo.memoryleak.MemoryLeakMainActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        findViewById<Button>(R.id.button_countdown_view).setOnClickListener {
            Intent(this, TestCountdownCircleView::class.java)
                .let {
                    this.startActivity(it)
                }
        }

        findViewById<Button>(R.id.button_memory_leak).setOnClickListener {
            Intent(this, MemoryLeakMainActivity::class.java)
                .let {
                    this.startActivity(it)
                }
        }
    }

}