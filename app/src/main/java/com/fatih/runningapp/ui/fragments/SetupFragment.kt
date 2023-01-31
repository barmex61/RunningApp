package com.fatih.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.fatih.runningapp.R
import com.fatih.runningapp.databinding.FragmentSetupBinding
import com.fatih.runningapp.util.Constants.KEY_FIRST_TIME_TOGGLE
import com.fatih.runningapp.util.Constants.KEY_NAME
import com.fatih.runningapp.util.Constants.KEY_WEIGHT
import com.fatih.runningapp.util.MyListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    private lateinit var binding:FragmentSetupBinding

    @Inject
    lateinit var sharedPref:SharedPreferences

    @set:Inject
    var isFirstAppOpen=true

    private lateinit var myListener: MyListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding=DataBindingUtil.bind(view)!!
        binding.tvContinue.setOnClickListener {
            val success=writePersonalDataToSharedPref()
            if(success){
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }else{
                Snackbar.make(requireView(),"Please fill all fields",Snackbar.LENGTH_LONG).show()
            }
        }
        myListener=requireActivity() as MyListener
        if(!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }
        super.onViewCreated(view, savedInstanceState)

    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weight =binding.etWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        val toolbarText = "Let's go, $name!"
        myListener.setTittle(toolbarText)
        return true
    }

}