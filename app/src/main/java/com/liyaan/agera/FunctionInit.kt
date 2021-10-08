package com.liyaan.agera

import com.google.android.agera.Function
import com.google.android.agera.MutableRepository
import com.google.android.agera.Result

class FunctionInit(operatorRepo:MutableRepository<String>)
    :Function<Pair<Int,Int>, Result<Int>> {
    private val mOperatorRepo:MutableRepository<String> = operatorRepo
    override fun apply(input: Pair<Int, Int>): Result<Int> {
        var result = 0
        when (mOperatorRepo.get()) {
            "+" -> result = input.first + input.second
            "-" -> result = input.first - input.second
            "*" -> result = input.first * input.second
            "/" -> result = if (input.second != 0) {
                input.first / input.second
            } else {
                return Result.failure<Int>(Throwable("除数不能为0")) //返回失败的Result
            }
        }
        return Result.success(result)//返回成功的Result
    }

}