package ru.netology.nmedia.util

object ViewUtils {
    fun formattedNumber(number: Int): String {
        return when {
            number < 1_000 -> number.toString()
            number < 10_000 -> "${number / 1000}" + (if ((number / 100) % 10 > 0) ".${(number / 100) % 10}" else "") + " K"
            number < 1_000_000 -> "${number / 1000} K"
            number < 10_000_000 -> "${number / 1_000_000}" + (if ((number / 100_000) % 10 > 0) ".${(number / 100_000) % 10}" else "") + " M"
            else -> "${number / 1_000_000} M"
        }
    }
}