package com.fatih.runningapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fatih.runningapp.R
import com.fatih.runningapp.databinding.ActivityMainBinding
import com.fatih.runningapp.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.fatih.runningapp.util.MyListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),MyListener {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController:NavController
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        setupNavController()
        intent.action?.let {
            if (it==ACTION_SHOW_TRACKING_FRAGMENT){
                navController.navigate(R.id.trackingFragment,null,NavOptions.Builder().setLaunchSingleTop(true).build())
            }
        }
        println("commit")
        setSupportActionBar(binding.toolbar)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.action?.let {
            if (it==ACTION_SHOW_TRACKING_FRAGMENT){
                navController.navigate(R.id.trackingFragment,null,NavOptions.Builder().setLaunchSingleTop(true).build())
            }
        }
    }

    private fun setupNavController(){
        navHostFragment=supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController=navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->

            when(destination.id){
                R.id.runFragment, R.id.settingsFragment , R.id.statisticsFragment->{
                    binding.bottomNavigationView.visibility= View.VISIBLE
                }
                else ->{
                    binding.bottomNavigationView.visibility=View.GONE
                }
            }
        }
    }

    override fun setTittle(tittle: String) {
        binding.tvToolbarTitle.text=tittle
    }
}