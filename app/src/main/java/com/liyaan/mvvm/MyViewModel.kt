package com.liyaan.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
const val NUMBER = "NUMBER"
class MyViewModel(var handle: SavedStateHandle):ViewModel() {
    init {
        if (!handle.contains("NUMBER")){
            handle.set(NUMBER,0)
        }
    }
    private var numberInt = MutableLiveData<Int>(0)
    fun getNumberInt():LiveData<Int> = handle.getLiveData(NUMBER)
    fun setNumberInt(number:Int){
        handle.set(NUMBER,number)
//        numberInt.value = number
    }
    fun add(){
        val number:Int? = handle.get<Int>(NUMBER)
        handle.set(NUMBER,number!!+1)
    }
}