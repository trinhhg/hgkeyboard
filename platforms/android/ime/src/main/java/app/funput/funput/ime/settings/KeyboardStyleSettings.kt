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
    val showBottomEmojiKey: Boolean = false,
    val showVniAccentsOnNumberRow: Boolean = true,
    val alwaysCapitalizeLabels: Boolean = false,
    val showCommaPeriodKeys: Boolean = true,
    val showLanguageSwitchKey: Boolean = false,
    val toolbarMode: KeyboardToolbarMode = KeyboardToolbarMode.SINGLE_BAR,
    val doubleSpacePeriodEnabled: Boolean = true,
    val spaceSwipeBehavior: SpaceSwipeBehavior = SpaceSwipeBehavior.MOVE_CURSOR,
    val longPressDelayMs: Int = DefaultLongPressDelayMs,
) {
    /** Single unified 44dp toolbar is always used so no extra vertical space is wasted. */
    val effectiveIsDualBar: Boolean
        get() = false

    fun toLayoutOptions(): KeyboardStyleOptions = KeyboardStyleOptions(
        showSecondarySymbols = showSecondarySymbols,
        showBottomEmojiKey = false,
        showVniAccentsOnNumberRow = showVniAccentsOnNumberRow,
        alwaysCapitalizeLabels = alwaysCapitalizeLabels,
        showCommaPeriodKeys = showCommaPeriodKeys,
        showLanguageSwitchKey = showLanguageSwitchKey,
        spaceSwipeSwitchesLanguage = spaceSwipeBehavior == SpaceSwipeBehavior.SWITCH_LANGUAGE,
        toolbarMode = KeyboardToolbarMode.SINGLE_BAR,
        isDualBar = false,
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
                showSecondarySymbols = prefs[SecondarySymbolsKey] ?: true,
                showBottomEmojiKey = false,
                showVniAccentsOnNumberRow = prefs[VniAccentsKey] ?: true,
                alwaysCapitalizeLabels = prefs[AlwaysCapitalizeKey] ?: false,
                showCommaPeriodKeys = prefs[CommaPeriodKey] ?: true,
                showLanguageSwitchKey = prefs[LanguageSwitchKey] ?: false,
                toolbarMode = KeyboardToolbarMode.SINGLE_BAR,
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

    /** Applies the style preset's theme without resetting user layout or row toggles. */
    suspend fun applyPreset(
        preset: KeyboardStylePreset,
        isDark: Boolean = false,
        themeIdOverride: KeyboardThemeId? = null,
    ) {
        dataStore.edit { prefs ->
            prefs[PresetKey] = preset.id
            val themeId = themeIdOverride ?: when (preset) {
                KeyboardStylePreset.LABAN -> if (isDark) KeyboardThemeId.LabanDark else KeyboardThemeId.Laban
                KeyboardStylePreset.SWIFTKEY -> if (isDark) KeyboardThemeId.SwiftKeyDark else KeyboardThemeId.SwiftKey
                KeyboardStylePreset.LIQUID_GLASS -> KeyboardThemeId.LiquidGlass
            }
            prefs[KeyboardThemeSettings.ThemeIdKey] = themeId.value
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
