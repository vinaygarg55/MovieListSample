package com.vinay.movielistsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.vinay.movielistsample.databinding.ActivityMainBinding
import com.vinay.movielistsample.ui.DataBindingActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : DataBindingActivity<ActivityMainBinding>() {

    override val layoutRes = R.layout.activity_main

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        navController = nav_host_fragment.findNavController()
//        navController.navigate(
//            R.id.movieListFragment
//        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return nav_host_fragment.findNavController().navigateUp() || super.onSupportNavigateUp()
    }
}
