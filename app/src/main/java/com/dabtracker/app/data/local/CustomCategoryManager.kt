package com.dabtracker.app.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomCategoryManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("custom_categories", Context.MODE_PRIVATE)

    private val _categories = MutableStateFlow(loadCategories())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    fun addCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        val current = loadCategories().toMutableSet()
        current.add(trimmed)
        prefs.edit().putStringSet(KEY, current).apply()
        _categories.value = current.sorted()
    }

    fun removeCategory(name: String) {
        val current = loadCategories().toMutableSet()
        current.remove(name)
        prefs.edit().putStringSet(KEY, current).apply()
        _categories.value = current.sorted()
    }

    private fun loadCategories(): List<String> {
        return prefs.getStringSet(KEY, emptySet())?.sorted() ?: emptyList()
    }

    companion object {
        private const val KEY = "categories"
    }
}
