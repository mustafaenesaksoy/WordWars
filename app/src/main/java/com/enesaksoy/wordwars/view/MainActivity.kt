package com.enesaksoy.wordwars.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentFactory
import com.enesaksoy.wordwars.R
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val fragmentFactory by inject<FragmentFactory>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = fragmentFactory
        setContentView(R.layout.activity_main)
    }
}