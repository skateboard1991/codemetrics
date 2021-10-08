package com.skateboard.codemetrics.checker

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

class KtExpressionChecker : NodeChecker() {
    override val nodeSet: MutableSet<Class<out PsiElement>>
        get() = mutableSetOf(
            KtIfExpression::class.java,
            KtWhileExpression::class.java,
            KtDoWhileExpression::class.java,
            KtForExpression::class.java,
            KtSafeQualifiedExpression::class.java
        )

    override fun check(element: PsiElement): Int {
        var nodeNum = 0
        if (isNode(element)) {
            nodeNum++
        }
        if (element is KtBinaryExpression && (KtTokens.ANDAND == element.operationToken || KtTokens.OROR == element.operationToken)) {
            nodeNum++
        }
        element.children.forEach {
            when (it) {
                is KtExpression -> {
                    nodeNum += check(it)
                }
                is KtContainerNodeForControlStructureBody, is KtProperty, is KtContainerNode, is KtLambdaArgument,is KtFunctionLiteral -> {
                    it.children.forEach { sub ->
                        if (sub is KtExpression) {
                            nodeNum += check(sub)
                        }
                    }
                }
                is KtWhenEntry -> {
                    it.children.forEach { sub ->
                        if (sub is KtWhenConditionWithExpression) {
                            nodeNum++
                        }
                    }
                }
                is KtCatchClause -> {
                    nodeNum++
                    it.children.forEach { sub ->
                        if (sub is KtExpression) {
                            nodeNum += check(sub)
                        }
                    }
                }
            }
        }
        return nodeNum
    }
}