package com.tancolo.memoryleak

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.tancolo.memoryleak.handler.TestMemoryLeakHandlerAndActivity

class MemoryLeakMainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MemoryLeakMainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_leak_main)

        Log.d(TAG, "===> onCreate()")
        initView()
    }

    private fun initView() {
        findViewById<Button>(R.id.start_handler_activity).setOnClickListener {
            Intent(this, TestMemoryLeakHandlerAndActivity::class.java)
                .let {
                    Log.d(TAG, "===> start activity()")
                    this.startActivity(it)
            }
        }
    }


}