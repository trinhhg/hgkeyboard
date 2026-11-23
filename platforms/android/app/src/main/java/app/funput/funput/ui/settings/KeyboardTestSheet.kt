package app.funput.funput.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import app.funput.funput.ui.kit.controls.FunputButton
import app.funput.funput.ui.kit.controls.FunputButtonStyle
import app.funput.funput.ui.kit.controls.FunputIconButton
import app.funput.funput.ui.kit.controls.FunputTextField
import app.funput.funput.ui.kit.icons.FunputIcons
import app.funput.funput.ui.kit.overlays.FunputSheet
import app.funput.funput.ui.kit.theme.FunputUi

/**
 * Top-bar keyboard button in Settings so the user can open a quick test input anytime to
 * verify layout, theme, toolbar icons, clipboard and Vietnamese predictions immediately.
 */
@Composable
internal fun TestKeyboardTopBarAction(onClick: () -> Unit) {
    FunputIconButton(
        icon = FunputIcons.Keyboard,
        contentDescription = "Thử bàn phím",
        onClick = onClick,
    )
}

/** Bottom sheet with a live multi-line input field for testing current Hg Keyboard settings. */
@Composable
internal fun KeyboardTestSheet(visible: Boolean, onDismiss: () -> Unit) {
    if (!visible) return
    var sampleText by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    FunputSheet(onDismiss = onDismiss, title = "Thử bàn phím") {
        FunputTextField(
            value = sampleText,
            onValueChange = { sampleText = it },
            label = "Gõ thử tại đây",
            placeholder = "Chạm để mở bàn phím và kiểm tra bố cục, gợi ý, thanh công cụ…",
            singleLine = false,
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.focusRequester(focusRequester),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(FunputUi.spacing.medium),
            modifier = Modifier.fillMaxWidth(),
        ) {
            FunputButton(
                text = "Xóa chữ",
                onClick = { sampleText = "" },
                style = FunputButtonStyle.SECONDARY,
                enabled = sampleText.isNotEmpty(),
                modifier = Modifier.weight(1f),
            )
            FunputButton(
                text = "Đóng",
                onClick = {
                    keyboard?.hide()
                    onDismiss()
                },
                style = FunputButtonStyle.PRIMARY,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
