package com.liyaan.mvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.liyaan.mynew.R
import com.liyaan.mynew.databinding.ActivityMvvmLayoutBinding

class MvvmActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel =
            ViewModelProvider(this,
                SavedStateViewModelFactory(application,this))[MyViewModel::class.java]
        val binding =
            setContentView(this, R.layout.activity_mvvm_layout)
                    as ActivityMvvmLayoutBinding
        binding.data = viewModel
        binding.lifecycleOwner = this
    }
}