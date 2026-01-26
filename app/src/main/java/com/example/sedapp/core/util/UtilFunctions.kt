package com.example.sedapp.core.util

import com.example.sedapp.domain.model.Currency

/**
 * Formats a price with its currency.
 * Standardizes to 2 decimal places for consistency across the app.
 */
fun getFormattedPrice(
    currency: Currency = Currency.RM,
    price: Double
): String {
    return "${currency.name} %.1f".format(price)
}
