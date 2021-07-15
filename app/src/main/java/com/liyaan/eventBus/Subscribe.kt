package com.liyaan.eventBus

import java.lang.annotation.*
import java.lang.annotation.Retention
import java.lang.annotation.Target

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
annotation class Subscribe(
    val threadMode: ThreadMode = ThreadMode.POSTING,
    /**
     * 是否是黏性事件
     * @return
     */
    val sticky: Boolean = false,
    /**
     * 优先级,值越大优先级越高
     * @retu
     */
    val priority: Int = 0
)