package app.funput.funput.ui.settings.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import app.funput.funput.R
import app.funput.funput.ime.settings.KeyboardStylePreferences
import app.funput.funput.ime.settings.KeyboardStylePreset
import app.funput.funput.keyboard.layout.KeyboardSizingProfile
import app.funput.funput.keyboard.model.KeyboardInputMethod
import app.funput.funput.keyboard.model.KeyboardToolbarMode
import app.funput.funput.keyboard.placement.KeyboardPlacementMode
import app.funput.funput.keyboard.placement.KeyboardPlacementPreferences
import app.funput.funput.keyboard.placement.OneHandedSide
import app.funput.funput.ui.kit.cards.FunputDivider
import app.funput.funput.ui.kit.cards.FunputSection
import app.funput.funput.ui.kit.icons.FunputIcons
import app.funput.funput.ui.kit.rows.FunputRowDefaults
import app.funput.funput.ui.kit.rows.LinkRow
import app.funput.funput.ui.kit.rows.SegmentedRow
import app.funput.funput.ui.kit.rows.SliderRow
import app.funput.funput.ui.kit.rows.ToggleRow
import app.funput.funput.ui.kit.theme.FunputTint
import app.funput.funput.ui.settings.label
import app.funput.funput.ui.settings.sectionTitle
import kotlin.math.roundToInt

/**
 * The keyboard's shape, Laban/SwiftKey toolbar/row switches, and 20:9 key size sliders.
 */
@Composable
internal fun LayoutSection(
    inputMethod: KeyboardInputMethod,
    showsNumberRow: Boolean,
    keyboardStyle: KeyboardStylePreferences = KeyboardStylePreferences.Default,
    keySizeProfile: KeyboardSizingProfile,
    placement: KeyboardPlacementPreferences,
    onShowsNumberRowChanged: (Boolean) -> Unit,
    onStylePresetSelected: (KeyboardStylePreset) -> Unit = {},
    onSecondarySymbolsChanged: (Boolean) -> Unit = {},
    onBottomEmojiKeyChanged: (Boolean) -> Unit = {},
    onToolbarModeSelected: (KeyboardToolbarMode) -> Unit = {},
    onVniAccentsChanged: (Boolean) -> Unit = {},
    onAlwaysCapitalizeLabelsChanged: (Boolean) -> Unit = {},
    onCommaPeriodKeysChanged: (Boolean) -> Unit = {},
    onLanguageSwitchKeyChanged: (Boolean) -> Unit = {},
    onKeySizeSelected: (KeyboardSizingProfile) -> Unit,
    onOpenPlacement: () -> Unit,
    onElevationSelected: (Float) -> Unit,
    onOneHandedWidthSelected: (Float) -> Unit,
    onOneHandedSideSelected: (OneHandedSide) -> Unit,
) {
    val divider = @Composable { FunputDivider(startInset = FunputRowDefaults.IconDividerInset) }
    FunputSection(
        title = sectionTitle(R.string.settings_section_layout),
        modifier = Modifier.testTag(LayoutSettingsSectionTag),
    ) {
        SegmentedRow(
            title = stringResource(R.string.settings_style_preset_title),
            options = listOf("Laban Key", "SwiftKey", "Glass"),
            selectedIndex = keyboardStyle.preset.ordinal,
            onSelect = { onStylePresetSelected(KeyboardStylePreset.entries[it]) },
            icon = FunputIcons.Appearance,
            tint = FunputTint.BLUE,
        )
        divider()
        SegmentedRow(
            title = stringResource(R.string.settings_toolbar_mode_title),
            options = listOf("Theo style", "1 thanh", "2 thanh"),
            selectedIndex = keyboardStyle.toolbarMode.ordinal,
            onSelect = { onToolbarModeSelected(KeyboardToolbarMode.entries[it]) },
            icon = FunputIcons.Suggestions,
            tint = FunputTint.BLUE,
        )
        divider()
        LinkRow(
            title = stringResource(R.string.settings_keyboard_mode_title),
            value = placement.activeMode.label(),
            icon = FunputIcons.Placement,
            tint = FunputTint.BLUE,
            onClick = onOpenPlacement,
        )
        when (placement.activeMode) {
            KeyboardPlacementMode.ELEVATED -> {
                divider()
                val maximum = elevationMaximumDp(inputMethod, keySizeProfile, showsNumberRow, placement)
                ElevationSliderRow(placement.elevatedOffsetDp, maximum, onElevationSelected)
            }
            KeyboardPlacementMode.ONE_HANDED -> {
                divider()
                PercentSliderRow(
                    title = stringResource(R.string.settings_one_handed_width_title),
                    icon = FunputIcons.OneHanded,
                    value = placement.oneHandedWidthFraction,
                    range = KeyboardPlacementPreferences.MinOneHandedWidthFraction..
                        KeyboardPlacementPreferences.MaxOneHandedWidthFraction,
                    onSettled = onOneHandedWidthSelected,
                )
                divider()
                SegmentedRow(
                    title = stringResource(R.string.settings_one_handed_side_title),
                    options = OneHandedSide.entries.map { it.label() },
                    selectedIndex = placement.oneHandedSide.ordinal,
                    onSelect = { onOneHandedSideSelected(OneHandedSide.entries[it]) },
                    icon = FunputIcons.OneHanded,
                    tint = FunputTint.BLUE,
                )
            }
            KeyboardPlacementMode.STANDARD -> Unit
        }
        if (inputMethod.isTelexFamily) {
            divider()
            ToggleRow(
                title = stringResource(R.string.settings_number_row_title),
                summary = stringResource(R.string.settings_number_row_summary),
                checked = showsNumberRow,
                onCheckedChange = onShowsNumberRowChanged,
                icon = FunputIcons.NumberRow,
                tint = FunputTint.BLUE,
            )
        } else {
            divider()
            ToggleRow(
                title = stringResource(R.string.settings_vni_accents_title),
                summary = stringResource(R.string.settings_vni_accents_summary),
                checked = keyboardStyle.showVniAccentsOnNumberRow,
                onCheckedChange = onVniAccentsChanged,
                icon = FunputIcons.NumberRow,
                tint = FunputTint.BLUE,
            )
        }
        divider()
        ToggleRow(
            title = stringResource(R.string.settings_secondary_symbols_title),
            summary = stringResource(R.string.settings_secondary_symbols_summary),
            checked = keyboardStyle.showSecondarySymbols,
            onCheckedChange = onSecondarySymbolsChanged,
            icon = FunputIcons.ToneMarks,
            tint = FunputTint.BLUE,
        )
        divider()
        ToggleRow(
            title = stringResource(R.string.settings_always_caps_title),
            summary = stringResource(R.string.settings_always_caps_summary),
            checked = keyboardStyle.alwaysCapitalizeLabels,
            onCheckedChange = onAlwaysCapitalizeLabelsChanged,
            icon = FunputIcons.Capitalize,
            tint = FunputTint.BLUE,
        )
        divider()
        ToggleRow(
            title = stringResource(R.string.settings_bottom_emoji_title),
            summary = stringResource(R.string.settings_bottom_emoji_summary),
            checked = keyboardStyle.showBottomEmojiKey,
            onCheckedChange = onBottomEmojiKeyChanged,
            icon = FunputIcons.Suggestions,
            tint = FunputTint.BLUE,
        )
        divider()
        ToggleRow(
            title = stringResource(R.string.settings_language_key_title),
            summary = stringResource(R.string.settings_language_key_summary),
            checked = keyboardStyle.showLanguageSwitchKey,
            onCheckedChange = onLanguageSwitchKeyChanged,
            icon = FunputIcons.Keyboard,
            tint = FunputTint.BLUE,
        )
        divider()
        ToggleRow(
            title = stringResource(R.string.settings_comma_period_title),
            summary = stringResource(R.string.settings_comma_period_summary),
            checked = keyboardStyle.showCommaPeriodKeys,
            onCheckedChange = onCommaPeriodKeysChanged,
            icon = FunputIcons.Gestures,
            tint = FunputTint.BLUE,
        )
        divider()
        PercentSliderRow(
            title = stringResource(R.string.settings_key_size_title),
            icon = FunputIcons.KeySize,
            value = keySizeProfile.heightScale,
            range = KeyboardSizingProfile.MinScale..KeyboardSizingProfile.MaxScale,
            onSettled = { scale ->
                onKeySizeSelected(
                    KeyboardSizingProfile.scaled(
                        scale = scale,
                        labelScale = keySizeProfile.labelScale,
                        bottomMarginDp = keySizeProfile.bottomMarginDp,
                    ),
                )
            },
            showsRange = true,
        )
        divider()
        PercentSliderRow(
            title = stringResource(R.string.settings_key_label_size_title),
            icon = FunputIcons.Capitalize,
            value = keySizeProfile.labelScale,
            range = 0.78f..1.28f,
            onSettled = { labelScale ->
                onKeySizeSelected(
                    KeyboardSizingProfile.scaled(
                        scale = keySizeProfile.heightScale,
                        labelScale = labelScale,
                        bottomMarginDp = keySizeProfile.bottomMarginDp,
                    ),
                )
            },
            showsRange = true,
        )
        divider()
        var marginDraft by remember(keySizeProfile.bottomMarginDp) { mutableFloatStateOf(keySizeProfile.bottomMarginDp) }
        SliderRow(
            title = stringResource(R.string.settings_bottom_margin_title),
            icon = FunputIcons.Placement,
            tint = FunputTint.BLUE,
            value = marginDraft,
            onValueChange = { marginDraft = it },
            onValueChangeFinished = {
                onKeySizeSelected(
                    KeyboardSizingProfile.scaled(
                        scale = keySizeProfile.heightScale,
                        labelScale = keySizeProfile.labelScale,
                        bottomMarginDp = marginDraft.roundToInt().toFloat(),
                    ),
                )
            },
            valueRange = 0f..24f,
            valueLabel = stringResource(R.string.settings_elevation_value, marginDraft.roundToInt()),
        )
    }
}

/** Test tag of the layout section. */
internal const val LayoutSettingsSectionTag = "settings-section-layout"
