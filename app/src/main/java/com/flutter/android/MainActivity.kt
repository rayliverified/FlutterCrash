package com.flutter.android

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var TAG = MainActivity::class.java.name

    lateinit var mFlutterEngine: FlutterEngine
    lateinit var mFlutterChannel: MethodChannel
    lateinit var mFragmentManager: FragmentManager
    var mFlutterFragment: FlutterFragment? = null
    val FLUTTER_FRAGMENT = "FLUTTER_FRAGMENT"
    val FLUTTER_ENGINE = "FLUTTER_ENGINE"
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this.application.applicationContext
        initFlutterEngine()

        //Create Flutter Fragment
        mFragmentManager = supportFragmentManager
        mFlutterFragment = mFragmentManager.findFragmentByTag(FLUTTER_FRAGMENT) as FlutterFragment?
        if (mFlutterFragment == null) {
            mFlutterFragment =
                FlutterFragment.withCachedEngine(FLUTTER_ENGINE).transparencyMode(FlutterView.TransparencyMode.opaque).build()
            mFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, mFlutterFragment as Fragment, FLUTTER_FRAGMENT)
                .commit()
        } else {
            mFragmentManager
                .beginTransaction()
                .show(mFragmentManager.findFragmentByTag(FLUTTER_FRAGMENT)!!)
                .commit()
        }

        //Create Flutter Method Channel.
        mFlutterChannel = MethodChannel(mFlutterEngine.dartExecutor, "app")

        button_1.setOnClickListener {
            mFragmentManager
                .beginTransaction()
                .show(mFragmentManager.findFragmentByTag(FLUTTER_FRAGMENT)!!)
                .commit()
            
            val handler = Handler()
            val r = Runnable {
                if (mFlutterFragment?.flutterEngine != null) {
                    Log.d("Flutter Engine", "Not Null")
                }
            }
            handler.postDelayed(r, 1000)
        }
    }

    override fun onBackPressed() {
        //Close Flutter Fragment
        if (mFragmentManager.findFragmentByTag(FLUTTER_FRAGMENT) != null) {
            mFragmentManager.beginTransaction().remove(mFragmentManager.findFragmentByTag(FLUTTER_FRAGMENT)!!).commit()
            return
        }
        super.onBackPressed()
    }

    fun initFlutterEngine(): FlutterEngine {
        if (!FlutterEngineCache.getInstance().contains(
                FLUTTER_ENGINE
            )
        ) {
            mFlutterEngine = FlutterEngine(context)
            mFlutterEngine.dartExecutor
                .executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
            FlutterEngineCache.getInstance()
                .put(FLUTTER_ENGINE, mFlutterEngine)
        }
        return mFlutterEngine
    }
}
