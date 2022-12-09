package com.example.sectionedrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var button: Button

    private lateinit var fragment1: Fragment1
    private lateinit var fragment2: Fragment2

    lateinit var viewModel: ViewModel

    var currentFragment = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
        setContentView(R.layout.activity_main)
        window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        viewModel.createData() // creates lists

        fragment1 = Fragment1()
        fragment2 = Fragment2()

        supportFragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment1).commit() // by default

        button = findViewById(R.id.button)
        button.text = "Current View: Fragment 1"
        button.setOnClickListener {
            currentFragment = if (currentFragment == 1) {
                supportFragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment2).commit()
                2
            } else {
                supportFragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment1).commit()
                1
            }
            button.text = "Current View: Fragment $currentFragment"
        }
    }
}