package com.gramasuvidha.portal.util

object ValidationUtil {
    fun validatePassword(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasNumber = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        val alphabetCount = password.count { it.isLetter() }
        return hasUppercase && hasNumber && hasSpecial && alphabetCount >= 3
    }
}
