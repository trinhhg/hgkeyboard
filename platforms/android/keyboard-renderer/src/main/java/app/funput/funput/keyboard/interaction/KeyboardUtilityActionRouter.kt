package app.funput.funput.keyboard.interaction

import app.funput.funput.keyboard.utility.KeyboardUtilityAction
import app.funput.funput.keyboard.utility.KeyboardUtilityDispatcher
import app.funput.funput.keyboard.model.KeyRole
import app.funput.funput.keyboard.model.KeySpec
import app.funput.funput.keyboard.model.SuggestionSelection

/** Routes toolbar utilities without adding their policies to pointer handling. */
internal class KeyboardUtilityActionRouter(
    private val onSuggestionSelected: (SuggestionSelection) -> Unit,
    private val utilities: KeyboardUtilityDispatcher,
    private val hasPasteOffer: () -> Boolean = { false },
) {
    fun dispatch(
        keyId: String?,
        key: KeySpec?,
        selection: SuggestionSelection?,
        onKeyboardKey: (String) -> Unit,
    ) {
        when {
            selection != null -> onSuggestionSelected(selection)
            key?.role == KeyRole.PLACEMENT -> utilities.dispatch(KeyboardUtilityAction.PLACEMENT)
            key?.role == KeyRole.CLIPBOARD -> utilities.dispatch(
                if (hasPasteOffer()) KeyboardUtilityAction.CLIPBOARD_PASTE
                else KeyboardUtilityAction.CLIPBOARD_PANEL,
            )
            key?.role == KeyRole.MICROPHONE -> utilities.dispatch(KeyboardUtilityAction.MICROPHONE)
            key?.role == KeyRole.EMOJI -> utilities.dispatch(KeyboardUtilityAction.EMOJI)
            key?.role == KeyRole.THEME -> utilities.dispatch(KeyboardUtilityAction.THEME)
            key?.role == KeyRole.SETTINGS -> utilities.dispatch(KeyboardUtilityAction.SETTINGS)
            key?.role == KeyRole.TOOLBAR_TOGGLE -> utilities.dispatch(KeyboardUtilityAction.TOOLBAR_TOGGLE)
            key?.role == KeyRole.NUMPAD -> utilities.dispatch(KeyboardUtilityAction.NUMPAD)
            key?.role == KeyRole.SHORTCUTS -> utilities.dispatch(KeyboardUtilityAction.SHORTCUTS)
            keyId != null -> onKeyboardKey(keyId)
        }
    }
}
