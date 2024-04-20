package com.tancolo.memoryleak.handler

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tancolo.memoryleak.R
import java.lang.ref.WeakReference

/**
 * The class mock the memory leak(or not) from an Activity with a bitmap picture, because a Handler hold the object
 * of this activity which should be destroyed. How to mock?
 * 1) Activity will show a bitmap about 2MB ~ 4MB with ImageView.
 * 2) Send a delay message (delay 60s) with a Handler which was created by 8 ways. [Strong Reference]
 * 3) Do or do not remove message when call onDestroy() in different CASES(1 ~ 8).
 * 4) Construct the Handler with a reference of the activity.
 * 5) open TestMemoryLeakHandlerAndActivity class and finish it, or repeat.
 *
 * How to show the memory leak?
 * Use inner class, send long delay message, do not remove callbacks and message.
 * To check the Heap Dump with Profiler tool in Android Studio. We can watch the memory leak directly.
 * The dump file locates in /data/... for example, hprof: heap dump "/data/local/tmp/perfd/cache/complete/28150514515100" starting...

 * * ===========The Best Practice===========================
 * 1) Use nest class(for Kotlin) ~= inner static class (for java)
 * 2) WeakReference<T>() ===> from the test, if the MessageQueue always held the Message,
 * Message held the Handler, handler held the WeakReference<Activity>, even use the WeakReference
 * for Activity which passed from constructor, the Activity still could not be collected by GC.
 * 3) xxxHandler.removeAllCallbackAndMessage(null)

 * ================Test Comparison 8 conditions=============
 * There are 4 factors: inner class, nest class, WeakReference<T>, removeCallbacksAndMessages()[remove for short]
 * ===> Test condition: 60s delay message. Test pre-test preparation see #How to mock# as above
 * 1) inner class                           ===> activity leak
 * 2) inner class + WeakReference           ===> activity leak
 * 3) inner class + remove                  ===> no activity leak
 * 4) inner class + WeakReference + remove  ===> no activity leak
 *
 * 5) nest class                            ===> activity leak
 * 6) nest class + WeakReference            ===> no activity leak
 * 7) nest class + remove                   ===> no activity leak
 * 8) nest class + WeakReference + remove   ===> no activity leak
 *
 */
class TestMemoryLeakHandlerAndActivity : AppCompatActivity() {

    private lateinit var mInnerClassHandlerCase1: InnerClassHandlerCase1
    private lateinit var mInnerClassHandlerCase2: InnerClassHandlerCase2
    private lateinit var mInnerClassHandlerCase3: InnerClassHandlerCase3
    private lateinit var mInnerClassHandlerCase4: InnerClassHandlerCase4

    private lateinit var mNestClassHandlerCase1: NestClassHandlerCase1
    private lateinit var mNestClassHandlerCase2: NestClassHandlerCase2
    private lateinit var mNestClassHandlerCase3: NestClassHandlerCase3
    private lateinit var mNestClassHandlerCase4: NestClassHandlerCase4

    companion object {
        const val TAG = "TestMemoryLeakHandlerAndActivity"
        const val MSG_BUTTON_CLICK = 1000
        const val MSG_DELAY_TIME = 20_000

        /**
         * This variable control the invocation of different test functions for different cases.
         * 1 - 8, default is 6
         */
        const val DEBUG_CASE_TYPE: Int = 1
    }

    /** 1) inner class ===> has activity leak
     * Inner class, not nest class, it will reproduce memory leak of Activity
     * Please note the abnormal background warning on InnerClassHandlerCase1 in the IDE,
     * Please see the tips, how to fix the warning of InnerClassHandlerCase1.
     */
    inner class InnerClassHandlerCase1(activity: AppCompatActivity) :
        Handler(Looper.getMainLooper()) {
        private val mActivity: AppCompatActivity

        init {
            mActivity = activity
        }

        override fun handleMessage(msg: Message) {
            println("Inner class case 1 ===> msg arg is ${msg.arg1}")
            Toast.makeText(
                this@TestMemoryLeakHandlerAndActivity,
                "Receive the delay ${msg.arg1} seconds message",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /** 2) inner class + WeakReference ===> activity leak
     * Inner class, not nest class, even with WeakReference
     * Please note the abnormal background warning on InnerClassHandlerCase2 in the IDE,
     * Please see the tips, how to fix the warning of InnerClassHandlerCase2.
     */
    inner class InnerClassHandlerCase2(activity: AppCompatActivity) :
        Handler(Looper.getMainLooper()) {
        private val mWeakReferenceActivity: WeakReference<AppCompatActivity>

        init {
            mWeakReferenceActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            println("Inner class case 2 ===> msg arg is ${msg.arg1}")
            Toast.makeText(
                this@TestMemoryLeakHandlerAndActivity,
                "Receive the delay ${msg.arg1} seconds message",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /** 3) inner class + remove ===> no activity leak
     * Inner class, not nest class, add removeCallbacksAndMessages() to clean all callbacks and messages on Destroy
     * Please note the abnormal background warning on InnerClassHandlerCase3 in the IDE,
     * Please see the tips, how to fix the warning of InnerClassHandlerCase3.
     * Not recommended way
     */
    inner class InnerClassHandlerCase3(activity: AppCompatActivity) :
        Handler(Looper.getMainLooper()) {
        private val mActivity: AppCompatActivity = activity

        override fun handleMessage(msg: Message) {
            println("Inner class case 3 ===> msg arg is ${msg.arg1}")
            Toast.makeText(
                mActivity,
                "Receive the delay ${msg.arg1} seconds message",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /** 4) inner class + WeakReference + remove  ===> no activity leak
     * Inner class, not nest class, + WeakReference + removeCallbacksAndMessages()
     * Please note the abnormal background warning on InnerClassHandlerCase4 in the IDE,
     * Please see the tips, how to fix the warning of InnerClassHandlerCase4.
     */
    inner class InnerClassHandlerCase4(activity: AppCompatActivity) :
        Handler(Looper.getMainLooper()) {
        private val mWeakReferenceActivity = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            println("Inner class case 4 ===> msg arg is ${msg.arg1}")
            Toast.makeText(
                mWeakReferenceActivity.get(),
                "Receive the delay ${msg.arg1} seconds message",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /** 5) nest class ===> activity leak
     * Nest class only
     */
    class NestClassHandlerCase1(activity: AppCompatActivity) : Handler(Looper.getMainLooper()) {
        private val mActivity: AppCompatActivity

        init {
            mActivity = activity
        }

        override fun handleMessage(msg: Message) {
            println("Nest class case 1 ===> msg arg is ${msg.arg1}")
            Toast.makeText(
                mActivity,
                "Receive the delay ${msg.arg1} seconds message",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /** 6) nest class + WeakReference ===> no activity leak
     * Nest class(inner static class for java) + WeakReference are the recommended approach in IntelliJ IDE
     */
    class NestClassHandlerCase2(activity: AppCompatActivity) : Handler(Looper.getMainLooper()) {
        /**
         * mWeakReferenceActivity hold a weak reference of xxxActivity, to access the member functions or variables.
         * like, mWeakReferenceActivity.get().doSomething()
         */
        private val mWeakReferenceActivity = WeakReference(activity)
        private val mWeakReferenceApplicationContext = WeakReference(activity.applicationContext)

        override fun handleMessage(msg: Message) {
            println("Nest class case 2 ===> msg arg is ${msg.arg1}")

            /**
             * Here is a potential bug (NullPointerException), after 60s(the message delays 60s),
             * the object of TestMemoryLeakHandlerAndActivity was collected by GC, when get the object
             * from mWeakReferenceActivity.get(), the get() will return null, but Toast should use
             * context(current is null) to show the message, so NPE happened.
             *
             * How to fix this type issue? User want to pass the object of Context or xxActivity to show Toast or sth. else.
             * Use WeakReference<applicationContext>.
             * Best Practice: Use the weak reference of application context in the nest class scope if needed.
             */
            Toast.makeText(
                //mWeakReferenceActivity.get(), // ERROR
                mWeakReferenceApplicationContext.get(), // CORRECT
                "Receive the delay ${msg.arg1} seconds message",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /** 7) nest class + remove ===> no activity leak
     * Nest class(inner static class for java) + removeCallbacksAndMessages() in onDestroy
     * This approach is not a good way.
     */
    class NestClassHandlerCase3(activity: AppCompatActivity) : Handler(Looper.getMainLooper()) {
        private val mActivity = activity

        override fun handleMessage(msg: Message) {
            println("Nest class case 3 ===> msg arg is ${msg.arg1}")
            Toast.makeText(
                mActivity,
                "Receive the delay ${msg.arg1} seconds message",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /** 8) nest class + WeakReference + remove ===> no activity leak
     * Nest class(inner static class for java) + WeakReference + remove all callback and message.
     * Well, from the test result, no need to add the remove
     */
    class NestClassHandlerCase4(activity: AppCompatActivity) : Handler(Looper.getMainLooper()) {
        private val mWeakReferenceActivity = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            println("Nest class case 4 ===> msg arg is ${msg.arg1}")
            Toast.makeText(
                mWeakReferenceActivity.get(),
                "Receive the delay ${msg.arg1} seconds message",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_memory_leak_handler_and)

        initViewAndTestCases()
    }

    private fun initViewAndTestCases() {
        when (DEBUG_CASE_TYPE) {
            1 -> testInnerClassHandlerCase1()
            2 -> testInnerClassHandlerCase2()
            3 -> testInnerClassHandlerCase3()
            4 -> testInnerClassHandlerCase4()
            5 -> testNestClassHandlerCase1()
            6 -> testNestClassHandlerCase2()
            7 -> testNestClassHandlerCase3()
            8 -> testNestClassHandlerCase4()
        }
    }

    /**
     * For testing case 1, 1) inner class ===> has activity leak
     */
    private fun testInnerClassHandlerCase1() {
        mInnerClassHandlerCase1 = InnerClassHandlerCase1(this)
        findViewById<Button>(R.id.btn_send_message).setOnClickListener {
            // Send message
            Message.obtain().let {
                it.arg1 = MSG_DELAY_TIME
                mInnerClassHandlerCase1.sendMessageDelayed(it, MSG_DELAY_TIME.toLong())
                Toast.makeText(this, "Send delay ${it.arg1} seconds message", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    /**
     * For testing case 2, 2) inner class + WeakReference ===> activity leak
     */
    private fun testInnerClassHandlerCase2() {
        mInnerClassHandlerCase2 = InnerClassHandlerCase2(this)
        findViewById<Button>(R.id.btn_send_message).setOnClickListener {
            // Send message
            Message.obtain().let {
                it.arg1 = MSG_DELAY_TIME
                mInnerClassHandlerCase2.sendMessageDelayed(it, MSG_DELAY_TIME.toLong())
                Toast.makeText(this, "Send delay ${it.arg1} seconds message", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    /**
     * For testing case 3, 3) inner class + remove ===> no activity leak?
     * Not recommended way
     */
    private fun testInnerClassHandlerCase3() {
        mInnerClassHandlerCase3 = InnerClassHandlerCase3(this)
        findViewById<Button>(R.id.btn_send_message).setOnClickListener {
            // Send message
            Message.obtain().let {
                it.arg1 = MSG_DELAY_TIME
                mInnerClassHandlerCase3.sendMessageDelayed(it, MSG_DELAY_TIME.toLong())
                Toast.makeText(this, "Send delay ${it.arg1} seconds message", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    /**
     * For testing case 4, 4) inner class + WeakReference + remove  ===> no activity leak
     * From the test result, no need to use remove[removeAllBackgroundAndMessage()]
     */
    private fun testInnerClassHandlerCase4() {
        mInnerClassHandlerCase4 = InnerClassHandlerCase4(this)
        findViewById<Button>(R.id.btn_send_message).setOnClickListener {
            // Send message
            Message.obtain().let {
                it.arg1 = MSG_DELAY_TIME
                mInnerClassHandlerCase4.sendMessageDelayed(it, MSG_DELAY_TIME.toLong())
                Toast.makeText(this, "Send delay ${it.arg1} seconds message", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    /**
     * For testing case 5, 5) nest class ===> activity leak
     * Nest class only, it will cause activity leak
     */
    private fun testNestClassHandlerCase1() {
        mNestClassHandlerCase1 = NestClassHandlerCase1(this)
        findViewById<Button>(R.id.btn_send_message).setOnClickListener {
            // Send message
            Message.obtain().let {
                it.arg1 = MSG_DELAY_TIME
                mNestClassHandlerCase1.sendMessageDelayed(it, MSG_DELAY_TIME.toLong())
                Toast.makeText(this, "Send delay ${it.arg1} seconds message", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    /**
     * For testing case 6, 6) nest class + WeakReference ===> no activity leak
     */
    private fun testNestClassHandlerCase2() {
        mNestClassHandlerCase2 = NestClassHandlerCase2(this)
        findViewById<Button>(R.id.btn_send_message).setOnClickListener {
            // Send message
            Message.obtain().let {
                it.arg1 = MSG_DELAY_TIME
                mNestClassHandlerCase2.sendMessageDelayed(it, MSG_DELAY_TIME.toLong())
                Toast.makeText(this, "Send delay ${it.arg1} seconds message", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    /**
     * For testing case 7, 7) nest class + remove ===> no activity leak
     * Not recommended way
     */
    private fun testNestClassHandlerCase3() {
        mNestClassHandlerCase3 = NestClassHandlerCase3(this)
        findViewById<Button>(R.id.btn_send_message).setOnClickListener {
            // Send message
            Message.obtain().let {
                it.arg1 = MSG_DELAY_TIME
                mNestClassHandlerCase3.sendMessageDelayed(it, MSG_DELAY_TIME.toLong())
                Toast.makeText(this, "Send delay ${it.arg1} seconds message", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    /**
     * For testing case 8, 8) nest class + WeakReference + remove ===> no activity leak
     * From the test result, no need to add remove [removeCallbacksAndMessages()]
     */
    private fun testNestClassHandlerCase4() {
        mNestClassHandlerCase4 = NestClassHandlerCase4(this)
        findViewById<Button>(R.id.btn_send_message).setOnClickListener {
            // Send message
            Message.obtain().let {
                it.arg1 = MSG_DELAY_TIME
                mNestClassHandlerCase4.sendMessageDelayed(it, MSG_DELAY_TIME.toLong())
                Toast.makeText(this, "Send delay ${it.arg1} seconds message", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "===>onDestroy()")

        // Remove all callback and message
        when (DEBUG_CASE_TYPE) {
            3 -> mInnerClassHandlerCase3.removeCallbacksAndMessages(null)
            4 -> mInnerClassHandlerCase4.removeCallbacksAndMessages(null)
            7 -> mNestClassHandlerCase3.removeCallbacksAndMessages(null)
            8 -> mNestClassHandlerCase4.removeCallbacksAndMessages(null)
        }

        super.onDestroy()
    }
}