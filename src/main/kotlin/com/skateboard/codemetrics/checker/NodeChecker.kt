package com.skateboard.codemetrics.checker

import com.intellij.psi.*

//用来进行节点判定
abstract class NodeChecker {
    protected open val nodeSet:MutableSet<Class<out PsiElement>> = mutableSetOf()

    //返回判定节点数量
    abstract fun check(element: PsiElement): Int

    open fun isNode(element: PsiElement): Boolean {
        nodeSet.forEach {
            if (it.isInstance(element)) {
                return true
            }
        }
        return false
    }
}