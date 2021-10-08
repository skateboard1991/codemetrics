package com.skateboard.codemetrics.checker

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

class KTNodeChecker : NodeChecker() {
    override val nodeSet: MutableSet<Class<out PsiElement>>
        get() = mutableSetOf(
            KtIfExpression::class.java,
            KtWhileExpression::class.java,
            KtDoWhileExpression::class.java,
            KtForExpression::class.java,
            KtSafeQualifiedExpression::class.java,
            KtWhenConditionWithExpression::class.java,
            KtCatchClause::class.java
        )

    override fun check(element: PsiElement): Int {
        var nodeNum = 0
        if (isNode(element)) {
            nodeNum++
        }
        element.children.forEach {
            if (it is KtBinaryExpression && (KtTokens.ANDAND == it.operationToken || KtTokens.OROR == it.operationToken)) {
                nodeNum++
            }
            nodeNum+=check(it)
        }
        return nodeNum
    }
}