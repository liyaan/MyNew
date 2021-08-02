package com.liyaan.glide.manager

import com.liyaan.glide.RequestManager

class EmptyRequestManagerTreeNode  : RequestManagerTreeNode{
    override fun getDescendants(): Set<RequestManager> {
        return emptySet()
    }
}