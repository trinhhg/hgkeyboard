package app.funput.funput.ime.lifecycle

import android.inputmethodservice.InputMethodService
import app.funput.funput.ime.ImeEditingSession
import app.funput.funput.ime.ImeSettingsController
import app.funput.funput.ime.hardware.HardwareKeyboard
import app.funput.funput.ime.settings.speech.VoiceInputSettings
import app.funput.funput.ime.shortcuts.ImeShortcutsController
import app.funput.funput.ime.shortcuts.createImeShortcutsController
import app.funput.funput.ime.speech.integration.settings.ImeSpeechSettingsBinding
import app.funput.funput.keyboard.model.ShiftState
import app.funput.funput.keyboard.ui.clipboard.KeyboardShortcutEntry
import kotlinx.coroutines.CoroutineScope

internal class ImeRuntime(
    val session: ImeEditingSession,
    val settings: ImeSettingsController,
    val shortcuts: ImeShortcutsController,
    val hardwareKeyboard: HardwareKeyboard,
) {
    // Observe only after the owner stores this runtime: flows may emit synchronously.
    fun observe(service: InputMethodService, scope: CoroutineScope, speechEnabled: (Boolean) -> Unit) {
        settings.observe(service, scope)
        ImeSpeechSettingsBinding(VoiceInputSettings(service).voiceInputEnabled, speechEnabled).observe(scope)
    }
}

internal fun createImeRuntime(
    service: InputMethodService,
    scope: CoroutineScope,
    views: ImeInputViewBinder,
    invalidateSpeech: () -> Unit,
): ImeRuntime {
    val session = createServiceEditingSession(
        service = service,
        scope = scope,
        view = { views.view },
        // The list of Enter-to-send apps can arrive after the first field has started.
        onEnterKeyChanged = views::update,
    )
    val settings = createServiceSettings(session, { views.view }, invalidateSpeech, views::update)
    return ImeRuntime(
        session = session,
        settings = settings,
        shortcuts = createImeShortcutsController(service, scope, session.actionHandler) { library ->
            val mapped = if (library.isEnabled) {
                library.entries.map { KeyboardShortcutEntry(it.trigger, it.expansion) }
            } else {
                emptyList()
            }
            session.updateShortcuts(mapped, views.view)
        },
        hardwareKeyboard = HardwareKeyboard.bind(
            service = service,
            session = session,
            scope = scope,
            currentShift = { views.view?.shiftState ?: ShiftState.OFF },
            setShift = { state -> views.view?.shiftState = state },
        ),
    )
}
