package app.funput.funput.ui.settings.smart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import app.funput.funput.R
import app.funput.funput.keyboard.model.SpaceSwipeBehavior
import app.funput.funput.ui.kit.cards.FunputDivider
import app.funput.funput.ui.kit.cards.FunputSection
import app.funput.funput.ui.kit.icons.FunputIcons
import app.funput.funput.ui.kit.icons.FunputListIcons
import app.funput.funput.ui.kit.rows.FunputRowDefaults
import app.funput.funput.ui.kit.rows.LinkRow
import app.funput.funput.ui.kit.rows.SegmentedRow
import app.funput.funput.ui.kit.rows.SliderRow
import app.funput.funput.ui.kit.rows.ToggleRow
import app.funput.funput.ui.kit.theme.FunputTint
import app.funput.funput.ui.kit.theme.FunputUi
import app.funput.funput.ui.settings.SettingsScreenState
import kotlin.math.roundToInt

/**
 * What Funput does on its own while you type, one switch per [SmartInputToggle]. The suggestion
 * bar's switches get a card of their own, with the way into the words it has learned; everything
 * that acts while typing shares the other.
 */
@Composable
internal fun SmartSection(state: SettingsScreenState, onOpenDictionary: () -> Unit = {}) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FunputUi.spacing.section),
        modifier = Modifier.testTag(SmartSettingsSectionTag),
    ) {
        FunputSection(
            title = stringResource(R.string.settings_smart_section_suggestions),
            footer = stringResource(R.string.settings_smart_section_suggestions_footer),
        ) {
            toggles(SmartInputToggle.suggestionBar, state)
            FunputDivider(startInset = FunputRowDefaults.IconDividerInset)
            LinkRow(
                title = stringResource(R.string.dictionary_title),
                summary = stringResource(R.string.dictionary_link_summary),
                onClick = onOpenDictionary,
                icon = FunputListIcons.Dictionary,
                tint = FunputTint.PURPLE,
            )
        }
        FunputSection(title = stringResource(R.string.settings_smart_section_typing)) {
            toggles(SmartInputToggle.typing, state)
            FunputDivider(startInset = FunputRowDefaults.IconDividerInset)
            ToggleRow(
                title = stringResource(R.string.settings_double_space_period_title),
                summary = stringResource(R.string.settings_double_space_period_summary),
                checked = state.keyboardStyle.doubleSpacePeriodEnabled,
                onCheckedChange = state.onDoubleSpacePeriodChanged,
                icon = FunputIcons.Gestures,
                tint = FunputTint.PURPLE,
            )
            FunputDivider(startInset = FunputRowDefaults.IconDividerInset)
            SegmentedRow(
                title = stringResource(R.string.settings_space_swipe_title),
                options = listOf("Di con trỏ", "Đổi ngôn ngữ"),
                selectedIndex = state.keyboardStyle.spaceSwipeBehavior.ordinal,
                onSelect = { state.onSpaceSwipeBehaviorSelected(SpaceSwipeBehavior.entries[it]) },
                icon = FunputIcons.Keyboard,
                tint = FunputTint.PURPLE,
            )
            FunputDivider(startInset = FunputRowDefaults.IconDividerInset)
            var delayDraft by remember(state.keyboardStyle.longPressDelayMs) {
                mutableFloatStateOf(state.keyboardStyle.longPressDelayMs.toFloat())
            }
            SliderRow(
                title = stringResource(R.string.settings_long_press_delay_title),
                icon = FunputIcons.Haptics,
                tint = FunputTint.PURPLE,
                value = delayDraft,
                onValueChange = { delayDraft = it },
                onValueChangeFinished = { state.onLongPressDelayChanged(delayDraft.roundToInt()) },
                valueRange = 180f..550f,
                valueLabel = "${delayDraft.roundToInt()} ms",
            )
        }
    }
}

@Composable
private fun ColumnScope.toggles(toggles: List<SmartInputToggle>, state: SettingsScreenState) {
    toggles.forEachIndexed { index, toggle ->
        if (index > 0) FunputDivider(startInset = FunputRowDefaults.IconDividerInset)
        ToggleRow(
            title = stringResource(toggle.title),
            summary = toggle.summary?.let { stringResource(it) },
            checked = toggle.isOn(state),
            onCheckedChange = { enabled -> toggle.set(state, enabled) },
            icon = toggle.icon,
            tint = FunputTint.PURPLE,
        )
    }
}

/** Test tag of the smart input section. */
internal const val SmartSettingsSectionTag = "settings-section-smart"
