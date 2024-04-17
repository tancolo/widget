package com.tancolo.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.tancolo.customview.countdownview.CountdownCircleView

class MainActivity : AppCompatActivity() {

    private val mCountdownCircleViewList = ArrayList<CountdownCircleView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        findViewById<Button>(R.id.button_show).setOnClickListener {
            // start all the countdown circle view.
            mCountdownCircleViewList.map {
                it.start()
            }
        }

        // Add all the countdown view to ArrayList
        // linearlayout 1
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_forward))
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_backward))
        mCountdownCircleViewList.add(findViewById(R.id.view_counterclockwise_forward))
        mCountdownCircleViewList.add(findViewById(R.id.view_counterclockwise_backward))

        // linearlayout 2
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_forward_2_001))
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_forward_2_002))
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_forward_2_003))
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_forward_2_004))

        // linearlayout 3
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_backward_2_001))
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_backward_2_002))
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_backward_2_003))
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_backward_2_004))

        // linearlayout 4
        mCountdownCircleViewList.add(findViewById(R.id.view_counterclockwise_forward_4_001))
        mCountdownCircleViewList.add(findViewById(R.id.view_counterclockwise_forward_4_002))
        mCountdownCircleViewList.add(findViewById(R.id.view_counterclockwise_forward_4_003))
        mCountdownCircleViewList.add(findViewById(R.id.view_counterclockwise_forward_4_004))

        // linearlayout 5
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_forward_5_001))
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_forward_5_002))
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_forward_5_003))
        mCountdownCircleViewList.add(findViewById(R.id.view_clockwise_forward_5_004))

    }

}