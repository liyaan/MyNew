package com.liyaan.glide.manager

import com.liyaan.glide.RequestManager

interface RequestManagerTreeNode {
    fun getDescendants(): Set<RequestManager>
}