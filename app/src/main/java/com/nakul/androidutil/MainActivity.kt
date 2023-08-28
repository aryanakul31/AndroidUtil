package com.nakul.androidutil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nakul.androidutil.databinding.ActivityMainBinding
import com.nakul.androidutil.location_helper.SampleLocationHelper
import com.nakul.androidutil.permission_helper.SamplePermissionHelper


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Permission Helper
        SamplePermissionHelper.permissionNotification(this)

        //Location Helper
//        setFragment(SampleLocationHelper())
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentView, fragment)
        fragmentTransaction.commit()
    }
}