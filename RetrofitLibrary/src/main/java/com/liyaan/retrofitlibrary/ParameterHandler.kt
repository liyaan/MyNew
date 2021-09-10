package com.liyaan.retrofitlibrary

abstract class ParameterHandler {
    protected var key: String? = null
    abstract fun apply(serviceMethod: ServiceMethod, value: String?)


    internal class QueryParameterHandler(key: String?) :
        ParameterHandler() {
        override fun apply(
            serviceMethod: ServiceMethod,
            value: String?
        ) {
            serviceMethod.addQueryParameterHandler(key, value)
        }

        init {
            this.key = key
        }

    }


    internal class FiledParameterHandler(key: String?) :
        ParameterHandler() {
        override fun apply(
            serviceMethod: ServiceMethod,
            value: String?
        ) {
            serviceMethod.addFiledParameter(key, value)
        }

        init {
            this.key = key
        }
    }
}