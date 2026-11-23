package app.funput.funput.ime.lifecycle

import android.inputmethodservice.InputMethodService
import app.funput.funput.ime.ImeEditingSession
import app.funput.funput.ime.ImeKeyboardCallbackBinder
import app.funput.funput.ime.ImePlacementBinder
import app.funput.funput.ime.ImeSettingsController
import app.funput.funput.ime.SystemInputMethodSwitcher
import app.funput.funput.ime.applyImeState
import app.funput.funput.ime.installedImeThemeRepository
import app.funput.funput.ime.isDarkAppearance
import app.funput.funput.ime.settings.KeyboardStylePreset
import app.funput.funput.ime.settings.KeyboardStyleSettings
import app.funput.funput.ime.settings.KeyboardThemeSettings
import app.funput.funput.ime.settings.KeyboardThemeSlot
import app.funput.funput.keyboard.ui.FunputKeyboardView
import app.funput.funput.keyboard.ui.emoji.EmojiCatalogPreloader
import app.funput.funput.theme.KeyboardThemeId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Owns view construction and binding; the service supplies lifecycle callbacks. */
internal class ImeInputViewBinder(
    private val service: InputMethodService,
    private val scope: CoroutineScope,
    private val session: () -> ImeEditingSession,
    private val settings: () -> ImeSettingsController,
) {
    private val switcher = SystemInputMethodSwitcher(service)
    private val themeRepository by lazy { service.installedImeThemeRepository() }
    var view: FunputKeyboardView? = null
        private set

    fun create(): FunputKeyboardView = FunputKeyboardView(service).also { keyboard ->
        view = keyboard
        update()
        val editing = session()
        ImeKeyboardCallbackBinder.bind(keyboard, editing, switcher)
        keyboard.callbacks.onThemeSelected = { themeId ->
            scope.launch {
                val styleStore = KeyboardStyleSettings(service)
                when (themeId) {
                    KeyboardThemeId.Laban -> styleStore.applyPreset(KeyboardStylePreset.LABAN, isDark = false)
                    KeyboardThemeId.LabanDark -> styleStore.applyPreset(KeyboardStylePreset.LABAN, isDark = true)
                    KeyboardThemeId.SwiftKey -> styleStore.applyPreset(KeyboardStylePreset.SWIFTKEY, isDark = false)
                    KeyboardThemeId.SwiftKeyDark -> styleStore.applyPreset(KeyboardStylePreset.SWIFTKEY, isDark = true)
                    KeyboardThemeId.LiquidGlass -> styleStore.applyPreset(KeyboardStylePreset.LIQUID_GLASS)
                    else -> KeyboardThemeSettings(service).setTheme(themeId, KeyboardThemeSlot.SINGLE)
                }
            }
        }
        ImePlacementBinder.bind(keyboard, service, scope)
        editing.bindPanels(keyboard)
        EmojiCatalogPreloader.schedule(keyboard)
    }

    fun update() {
        val keyboard = view ?: return
        val editing = session()
        keyboard.applyImeState(settings(), editing.editorRuntime.policy,
            editing.actionHandler.language, themeRepository, service.isDarkAppearance())
        editing.actionHandler.smartGesturesEnabled = keyboard.areSmartGesturesEnabled
    }

    fun detach() {
        view = null
    }
}
