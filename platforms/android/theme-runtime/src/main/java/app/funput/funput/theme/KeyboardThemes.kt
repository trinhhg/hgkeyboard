package app.funput.funput.theme

/**
 * Named keyboard theme presets.
 *
 * Preset names describe the design; the persisted identifiers in [KeyboardThemeId] stay `dark` and
 * `light` for the first pair because they are what the appearance setting stores on device. A
 * preset that carries its own palette lives in its own file next to this one and is exposed here,
 * so callers still have a single place to look one up.
 */
object KeyboardThemes {
    private const val AccentGold = 0xFFC8A951.toInt()

    /** Dark blue-grey preset with solid Gboard-inspired key surfaces and a teal Enter key. */
    val Slate: KeyboardTheme = SlateKeyboardTheme

    /**
     * Dark preset. Borderless, with key plates carried by a faint white wash rather than a solid
     * color: translucency keeps the plates reading correctly over the background gradient, and
     * over a background image if one is set. Modifier keys wash in at half the strength of the
     * alphabet so the letters stay dominant while every key still shows its touch area.
     */
    val Ink: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFF121013.toInt(),
        backgroundEndColor = 0xFF0A0A0C.toInt(),
        keyColor = 0x14FFFFFF,
        specialKeyColor = 0x0AFFFFFF,
        keyBorderColor = 0x00000000,
        keyShadowColor = 0x00000000,
        pressedKeyColor = 0x59C8A951,
        pressedKeyBorderColor = 0x00000000,
        activatedKeyColor = 0x3DC8A951,
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFFF2F2F6.toInt(),
        secondaryLabelColor = 0xFF83838E.toInt(),
        accentColor = AccentGold,
        keyCornerRadiusDp = 6f,
        keyBorderWidthDp = 0f,
        keyShadowOffsetDp = 0f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFF83838E.toInt(),
        accentKeyColor = AccentGold,
        accentLabelColor = 0xFF17110A.toInt(),
        popupSurfaceColor = 0xFF232026.toInt(),
        suggestionHighlightColor = 0xFFE3C371.toInt(),
        pressedKeyScale = 1.06f,
    )

    /** Light preset. Warm paper ground, white keycaps, and a single crisp contact shadow. */
    val Paper: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFFF3EFE7.toInt(),
        backgroundEndColor = 0xFFE8E2D6.toInt(),
        keyColor = 0xFFFFFFFF.toInt(),
        specialKeyColor = 0xFFDED7C9.toInt(),
        keyBorderColor = 0x00000000,
        keyShadowColor = 0x29352C1E,
        pressedKeyColor = AccentGold,
        pressedKeyBorderColor = 0x00000000,
        activatedKeyColor = AccentGold,
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFF1C1913.toInt(),
        secondaryLabelColor = 0xFF6B6459.toInt(),
        accentColor = 0xFFB08F32.toInt(),
        keyCornerRadiusDp = 6f,
        keyBorderWidthDp = 0f,
        keyShadowOffsetDp = 1.5f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFF4C463A.toInt(),
        accentKeyColor = 0xFFB08F32.toInt(),
        // Dark rather than cream: cream on this gold measures 2.9:1, under the readability bar.
        accentLabelColor = 0xFF17110A.toInt(),
        popupSurfaceColor = 0xFFFFFFFF.toInt(),
        suggestionHighlightColor = 0xFF8A6D18.toInt(),
        pressedKeyScale = 1f,
    )

    /** Dark glass preset: translucent plates over a diagonal midnight gradient. */
    val GlassDark: KeyboardTheme = GlassDarkKeyboardTheme

    /** Light glass preset from Funput Glass for iOS: cool plates on a blue-grey ground. */
    val GlassLight: KeyboardTheme = GlassLightKeyboardTheme

    /** Apple's Liquid Glass: clear lensed panes over a reflective ground. */
    val LiquidGlass: KeyboardTheme = LiquidGlassKeyboardTheme

    /** Laban Key light preset (derived from Laban Key lxx_light tokens). */
    val Laban: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFFECEFF1.toInt(),
        backgroundEndColor = 0xFFE4E8EC.toInt(),
        keyColor = 0xFFFFFFFF.toInt(),
        specialKeyColor = 0xFFD8DFE6.toInt(),
        keyBorderColor = 0x1A37474F,
        keyShadowColor = 0x26000000,
        pressedKeyColor = 0xFFE3F2FD.toInt(),
        pressedKeyBorderColor = 0xFF2095F4.toInt(),
        activatedKeyColor = 0xFF2095F4.toInt(),
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFF37474F.toInt(),
        secondaryLabelColor = 0xB337474F.toInt(),
        accentColor = 0xFF2095F4.toInt(),
        keyCornerRadiusDp = 5f,
        keyBorderWidthDp = 0.5f,
        keyShadowOffsetDp = 1.2f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFF37474F.toInt(),
        accentKeyColor = 0xFF2095F4.toInt(),
        accentLabelColor = 0xFFFFFFFF.toInt(),
        popupSurfaceColor = 0xFFFFFFFF.toInt(),
        suggestionHighlightColor = 0xFF2095F4.toInt(),
        pressedKeyScale = 1.02f,
        suggestionDividerColor = 0x2437474F,
        popupShadowColor = 0x33000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
    )

    /** Laban Key dark preset (derived from Laban Key lxx_dark tokens). */
    val LabanDark: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFF1F2227.toInt(),
        backgroundEndColor = 0xFF181A1E.toInt(),
        keyColor = 0xFF32363E.toInt(),
        specialKeyColor = 0xFF262A30.toInt(),
        keyBorderColor = 0x14FFFFFF,
        keyShadowColor = 0x33000000,
        pressedKeyColor = 0xFF434954.toInt(),
        pressedKeyBorderColor = 0xFF2095F4.toInt(),
        activatedKeyColor = 0xFF2095F4.toInt(),
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFFE6FFFFFF.toInt(),
        secondaryLabelColor = 0x99FFFFFF.toInt(),
        accentColor = 0xFF2095F4.toInt(),
        keyCornerRadiusDp = 5f,
        keyBorderWidthDp = 0.5f,
        keyShadowOffsetDp = 1.2f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xCCFFFFFF.toInt(),
        accentKeyColor = 0xFF2095F4.toInt(),
        accentLabelColor = 0xFFFFFFFF.toInt(),
        popupSurfaceColor = 0xFF3A3F48.toInt(),
        suggestionHighlightColor = 0xFF64B5F6.toInt(),
        pressedKeyScale = 1.02f,
        suggestionDividerColor = 0x24FFFFFF,
        popupShadowColor = 0x55000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
    )

    /** Microsoft SwiftKey Fluent Light preset (from SwiftKey Fluent Adaptive Light). */
    val SwiftKey: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFFF1F1F1.toInt(),
        backgroundEndColor = 0xFFECEEF1.toInt(),
        keyColor = 0xFFFFFFFF.toInt(),
        specialKeyColor = 0xFFE3E6EA.toInt(),
        keyBorderColor = 0xFFCCCCCC.toInt(),
        keyShadowColor = 0x1E000000,
        pressedKeyColor = 0xFFE3E3E3.toInt(),
        pressedKeyBorderColor = 0xFFBBBBBB.toInt(),
        activatedKeyColor = 0xFF3961ED.toInt(),
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFF292929.toInt(),
        secondaryLabelColor = 0xFF777777.toInt(),
        accentColor = 0xFF3961ED.toInt(),
        keyCornerRadiusDp = 5f,
        keyBorderWidthDp = 0.7f,
        keyShadowOffsetDp = 1.0f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFF292929.toInt(),
        accentKeyColor = 0xFF3961ED.toInt(),
        accentLabelColor = 0xFFFFFFFF.toInt(),
        popupSurfaceColor = 0xFFFFFFFF.toInt(),
        suggestionHighlightColor = 0xFF292929.toInt(),
        pressedKeyScale = 1.03f,
        suggestionDividerColor = 0x22000000,
        popupShadowColor = 0x2A000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
    )

    /** Microsoft SwiftKey Fluent Dark preset (from SwiftKey Fluent Adaptive Dark). */
    val SwiftKeyDark: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFF141414.toInt(),
        backgroundEndColor = 0xFF121212.toInt(),
        keyColor = 0xFF292929.toInt(),
        specialKeyColor = 0xFF1E1E1E.toInt(),
        keyBorderColor = 0xFF363636.toInt(),
        keyShadowColor = 0x33000000,
        pressedKeyColor = 0xFF3A3A3A.toInt(),
        pressedKeyBorderColor = 0xFF4D4D4D.toInt(),
        activatedKeyColor = 0xFF9DC4FF.toInt(),
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFFFFFFFF.toInt(),
        secondaryLabelColor = 0xFF8E8E8E.toInt(),
        accentColor = 0xFF9DC4FF.toInt(),
        keyCornerRadiusDp = 5f,
        keyBorderWidthDp = 0.6f,
        keyShadowOffsetDp = 1.0f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFFE5E5E5.toInt(),
        accentKeyColor = 0xFF3961ED.toInt(),
        accentLabelColor = 0xFFFFFFFF.toInt(),
        popupSurfaceColor = 0xFF3A3A3A.toInt(),
        suggestionHighlightColor = 0xFFFFFFFF.toInt(),
        pressedKeyScale = 1.03f,
        suggestionDividerColor = 0x28FFFFFF,
        popupShadowColor = 0x55000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
    )

    /** Light floral preset: a blush-to-lilac gradient under near-white keycaps. */
    val Blossom: KeyboardTheme = BlossomKeyboardTheme

    /** Dark floral preset: an orchid-to-midnight gradient under translucent plates. */
    val Orchid: KeyboardTheme = OrchidKeyboardTheme
}
