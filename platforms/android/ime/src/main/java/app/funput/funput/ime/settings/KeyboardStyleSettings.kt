package app.funput.funput.ime.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import app.funput.funput.keyboard.model.KeyboardStyleOptions
import app.funput.funput.keyboard.model.KeyboardToolbarMode
import app.funput.funput.keyboard.model.SpaceSwipeBehavior
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
    val showVniAccentsOnNumberRow: Boolean = true,
    val alwaysCapitalizeLabels: Boolean = false,
    val showCommaPeriodKeys: Boolean = true,
    val showLanguageSwitchKey: Boolean = false,
    val toolbarMode: KeyboardToolbarMode = KeyboardToolbarMode.AUTO_PRESET,
    val doubleSpacePeriodEnabled: Boolean = true,
    val spaceSwipeBehavior: SpaceSwipeBehavior = SpaceSwipeBehavior.MOVE_CURSOR,
    val longPressDelayMs: Int = DefaultLongPressDelayMs,
) {
    val effectiveIsDualBar: Boolean
        get() = when (toolbarMode) {
            KeyboardToolbarMode.AUTO_PRESET -> preset == KeyboardStylePreset.SWIFTKEY
            KeyboardToolbarMode.SINGLE_BAR -> false
            KeyboardToolbarMode.DUAL_BAR -> true
        }

    fun toLayoutOptions(): KeyboardStyleOptions = KeyboardStyleOptions(
        showSecondarySymbols = showSecondarySymbols,
        showBottomEmojiKey = showBottomEmojiKey,
        showVniAccentsOnNumberRow = showVniAccentsOnNumberRow,
        alwaysCapitalizeLabels = alwaysCapitalizeLabels,
        showCommaPeriodKeys = showCommaPeriodKeys,
        showLanguageSwitchKey = showLanguageSwitchKey,
        spaceSwipeSwitchesLanguage = spaceSwipeBehavior == SpaceSwipeBehavior.SWITCH_LANGUAGE,
        toolbarMode = toolbarMode,
        isDualBar = effectiveIsDualBar,
    )

    companion object {
        const val DefaultLongPressDelayMs = 350
        val Default = KeyboardStylePreferences()
    }
}

/** Persists Hg Keyboard style preset and layout switches shared between Settings and IME. */
class KeyboardStyleSettings(context: Context) {
    private val dataStore = context.applicationContext.funputSettingsStore

    val preferences: Flow<KeyboardStylePreferences> = dataStore.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { prefs ->
            val preset = KeyboardStylePreset.fromId(prefs[PresetKey])
            KeyboardStylePreferences(
                preset = preset,
                showSecondarySymbols = prefs[SecondarySymbolsKey] ?: (preset != KeyboardStylePreset.LABAN),
                showBottomEmojiKey = prefs[BottomEmojiKey] ?: (preset != KeyboardStylePreset.LABAN),
                showVniAccentsOnNumberRow = prefs[VniAccentsKey] ?: true,
                alwaysCapitalizeLabels = prefs[AlwaysCapitalizeKey] ?: false,
                showCommaPeriodKeys = prefs[CommaPeriodKey] ?: true,
                showLanguageSwitchKey = prefs[LanguageSwitchKey] ?: false,
                toolbarMode = prefs[ToolbarModeKey]?.let { raw ->
                    KeyboardToolbarMode.entries.firstOrNull { it.name == raw }
                } ?: KeyboardToolbarMode.AUTO_PRESET,
                doubleSpacePeriodEnabled = prefs[DoubleSpacePeriodKey] ?: true,
                spaceSwipeBehavior = prefs[SpaceSwipeKey]?.let { raw ->
                    SpaceSwipeBehavior.entries.firstOrNull { it.name == raw }
                } ?: SpaceSwipeBehavior.MOVE_CURSOR,
                longPressDelayMs = (prefs[LongPressDelayKey] ?: KeyboardStylePreferences.DefaultLongPressDelayMs)
                    .coerceIn(180, 600),
            )
        }
        .distinctUntilChanged()

    suspend fun setSecondarySymbols(enabled: Boolean) {
        dataStore.edit { it[SecondarySymbolsKey] = enabled }
    }

    suspend fun setBottomEmojiKey(enabled: Boolean) {
        dataStore.edit { it[BottomEmojiKey] = enabled }
    }

    suspend fun setVniAccentsOnNumberRow(enabled: Boolean) {
        dataStore.edit { it[VniAccentsKey] = enabled }
    }

    suspend fun setAlwaysCapitalizeLabels(enabled: Boolean) {
        dataStore.edit { it[AlwaysCapitalizeKey] = enabled }
    }

    suspend fun setCommaPeriodKeys(enabled: Boolean) {
        dataStore.edit { it[CommaPeriodKey] = enabled }
    }

    suspend fun setLanguageSwitchKey(enabled: Boolean) {
        dataStore.edit { it[LanguageSwitchKey] = enabled }
    }

    suspend fun setToolbarMode(mode: KeyboardToolbarMode) {
        dataStore.edit { it[ToolbarModeKey] = mode.name }
    }

    suspend fun setDoubleSpacePeriodEnabled(enabled: Boolean) {
        dataStore.edit { it[DoubleSpacePeriodKey] = enabled }
    }

    suspend fun setSpaceSwipeBehavior(behavior: SpaceSwipeBehavior) {
        dataStore.edit { it[SpaceSwipeKey] = behavior.name }
    }

    suspend fun setLongPressDelayMs(delayMs: Int) {
        dataStore.edit { it[LongPressDelayKey] = delayMs.coerceIn(180, 600) }
    }

    /** Applies a complete style preset: theme, number row, secondary symbols, toolbar, and bottom row. */
    suspend fun applyPreset(
        preset: KeyboardStylePreset,
        isDark: Boolean = false,
        themeIdOverride: KeyboardThemeId? = null,
    ) {
        dataStore.edit { prefs ->
            prefs[PresetKey] = preset.id
            prefs[ToolbarModeKey] = KeyboardToolbarMode.AUTO_PRESET.name
            when (preset) {
                KeyboardStylePreset.LABAN -> {
                    prefs[SecondarySymbolsKey] = false
                    prefs[BottomEmojiKey] = false
                    prefs[booleanPreferencesKey("shows_number_row")] = true
                    val themeId = themeIdOverride ?: if (isDark) KeyboardThemeId.LabanDark else KeyboardThemeId.Laban
                    prefs[KeyboardThemeSettings.ThemeIdKey] = themeId.value
                }
                KeyboardStylePreset.SWIFTKEY -> {
                    prefs[SecondarySymbolsKey] = true
                    prefs[BottomEmojiKey] = true
                    prefs[booleanPreferencesKey("shows_number_row")] = false
                    val themeId = themeIdOverride ?: if (isDark) KeyboardThemeId.SwiftKeyDark else KeyboardThemeId.SwiftKey
                    prefs[KeyboardThemeSettings.ThemeIdKey] = themeId.value
                }
                KeyboardStylePreset.LIQUID_GLASS -> {
                    prefs[SecondarySymbolsKey] = true
                    prefs[BottomEmojiKey] = true
                    prefs[KeyboardThemeSettings.ThemeIdKey] = (themeIdOverride ?: KeyboardThemeId.LiquidGlass).value
                }
            }
        }
    }

    companion object {
        private val PresetKey = stringPreferencesKey("keyboard_style_preset")
        private val SecondarySymbolsKey = booleanPreferencesKey("show_secondary_symbols")
        private val BottomEmojiKey = booleanPreferencesKey("show_bottom_emoji_key")
        private val VniAccentsKey = booleanPreferencesKey("show_vni_accents_number_row")
        private val AlwaysCapitalizeKey = booleanPreferencesKey("always_capitalize_labels")
        private val CommaPeriodKey = booleanPreferencesKey("show_comma_period_keys")
        private val LanguageSwitchKey = booleanPreferencesKey("show_language_switch_key")
        private val ToolbarModeKey = stringPreferencesKey("keyboard_toolbar_mode")
        private val DoubleSpacePeriodKey = booleanPreferencesKey("double_space_period_enabled")
        private val SpaceSwipeKey = stringPreferencesKey("space_swipe_behavior")
        private val LongPressDelayKey = intPreferencesKey("long_press_delay_ms")
    }
}
