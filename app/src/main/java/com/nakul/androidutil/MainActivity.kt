package com.nakul.androidutil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nakul.androidutil.databinding.ActivityMainBinding
import com.nakul.androidutil.permission_helper.SamplePermissionHelper

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        SamplePermissionHelper.permissionCamera(this)
    }
}