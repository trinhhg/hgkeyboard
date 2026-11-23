package app.funput.funput.ime.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import app.funput.funput.keyboard.model.KeyboardStyleOptions
import app.funput.funput.theme.KeyboardThemeId
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** High-level UI & layout preset matching the three supported Hg Keyboard personalities. */
enum class KeyboardStylePreset(val id: String) {
    LABAN("laban"),
    SWIFTKEY("swiftkey"),
    LIQUID_GLASS("liquid_glass"),
    ;

    companion object {
        fun fromId(raw: String?): KeyboardStylePreset =
            entries.firstOrNull { it.id == raw } ?: SWIFTKEY
    }
}

data class KeyboardStylePreferences(
    val preset: KeyboardStylePreset = KeyboardStylePreset.SWIFTKEY,
    val showSecondarySymbols: Boolean = true,
    val showBottomEmojiKey: Boolean = true,
) {
    fun toLayoutOptions(): KeyboardStyleOptions = KeyboardStyleOptions(
        showSecondarySymbols = showSecondarySymbols,
        showBottomEmojiKey = showBottomEmojiKey,
    )

    companion object {
        val Default = KeyboardStylePreferences()
    }
}

/** Persists Hg Keyboard style preset and layout switches shared between Settings and IME. */
class KeyboardStyleSettings(context: Context) {
    private val dataStore = context.applicationContext.funputSettingsStore

    val preferences: Flow<KeyboardStylePreferences> = dataStore.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { prefs ->
            KeyboardStylePreferences(
                preset = KeyboardStylePreset.fromId(prefs[PresetKey]),
                showSecondarySymbols = prefs[SecondarySymbolsKey] ?: true,
                showBottomEmojiKey = prefs[BottomEmojiKey] ?: true,
            )
        }
        .distinctUntilChanged()

    suspend fun setSecondarySymbols(enabled: Boolean) {
        dataStore.edit { it[SecondarySymbolsKey] = enabled }
    }

    suspend fun setBottomEmojiKey(enabled: Boolean) {
        dataStore.edit { it[BottomEmojiKey] = enabled }
    }

    /** Applies a complete style preset: theme, number row, secondary symbols, and bottom row. */
    suspend fun applyPreset(preset: KeyboardStylePreset, isDark: Boolean = false) {
        dataStore.edit { prefs ->
            prefs[PresetKey] = preset.id
            when (preset) {
                KeyboardStylePreset.LABAN -> {
                    prefs[SecondarySymbolsKey] = false
                    prefs[BottomEmojiKey] = false
                    prefs[booleanPreferencesKey("shows_number_row")] = true
                    val themeId = if (isDark) KeyboardThemeId.LabanDark else KeyboardThemeId.Laban
                    prefs[KeyboardThemeSettings.ThemeIdKey] = themeId.value
                }
                KeyboardStylePreset.SWIFTKEY -> {
                    prefs[SecondarySymbolsKey] = true
                    prefs[BottomEmojiKey] = true
                    prefs[booleanPreferencesKey("shows_number_row")] = false
                    val themeId = if (isDark) KeyboardThemeId.SwiftKeyDark else KeyboardThemeId.SwiftKey
                    prefs[KeyboardThemeSettings.ThemeIdKey] = themeId.value
                }
                KeyboardStylePreset.LIQUID_GLASS -> {
                    prefs[SecondarySymbolsKey] = true
                    prefs[BottomEmojiKey] = true
                    prefs[KeyboardThemeSettings.ThemeIdKey] = KeyboardThemeId.LiquidGlass.value
                }
            }
        }
    }

    companion object {
        private val PresetKey = stringPreferencesKey("keyboard_style_preset")
        private val SecondarySymbolsKey = booleanPreferencesKey("show_secondary_symbols")
        private val BottomEmojiKey = booleanPreferencesKey("show_bottom_emoji_key")
    }
}
