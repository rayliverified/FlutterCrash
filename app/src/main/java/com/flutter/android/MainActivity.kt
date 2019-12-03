package com.flutter.android

import android.content.Context
import android.os.Bundle
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
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this.application.applicationContext

        //Create Flutter Engine.
        mFlutterEngine = FlutterEngine(context)
        mFlutterEngine
            .dartExecutor
            .executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )
        //Add FlutterEngine to cache.
        FlutterEngineCache
            .getInstance()
            .put("FLUTTER_ENGINE", mFlutterEngine)
        //Create Flutter Method Channel.
        mFlutterChannel = MethodChannel(mFlutterEngine.dartExecutor, "app")
        //Create Flutter Fragment
        mFragmentManager = supportFragmentManager
        mFlutterFragment = mFragmentManager.findFragmentByTag(FLUTTER_FRAGMENT) as FlutterFragment?
        if (mFlutterFragment == null) {
            mFlutterFragment =
                FlutterFragment.withCachedEngine("FLUTTER_ENGINE").transparencyMode(FlutterView.TransparencyMode.transparent).build()
        }

        button_1.setOnClickListener {
            //Open Flutter Fragment.
            mFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, mFlutterFragment as Fragment, FLUTTER_FRAGMENT)
                .commit();
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
}
