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

    /** Laban Key preset: light grey background, crisp white keys, distinct blue accent. */
    val Laban: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFFECEEF1.toInt(),
        backgroundEndColor = 0xFFE2E4E8.toInt(),
        keyColor = 0xFFFFFFFF.toInt(),
        specialKeyColor = 0xFFD8DCE2.toInt(),
        keyBorderColor = 0x00000000,
        keyShadowColor = 0x22000000,
        pressedKeyColor = 0xFFE0E7FF.toInt(),
        pressedKeyBorderColor = 0xFF2563EB.toInt(),
        activatedKeyColor = 0xFF2563EB.toInt(),
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFF1E293B.toInt(),
        secondaryLabelColor = 0xFF64748B.toInt(),
        accentColor = 0xFF2563EB.toInt(),
        keyCornerRadiusDp = 7f,
        keyBorderWidthDp = 0f,
        keyShadowOffsetDp = 1.2f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFF334155.toInt(),
        accentKeyColor = 0xFF2563EB.toInt(),
        accentLabelColor = 0xFFFFFFFF.toInt(),
        popupSurfaceColor = 0xFFFFFFFF.toInt(),
        suggestionHighlightColor = 0xFF2563EB.toInt(),
        pressedKeyScale = 1.02f,
        suggestionDividerColor = 0x1E000000,
        popupShadowColor = 0x33000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
    )

    /** Microsoft SwiftKey preset: clean light grey, rounded keycaps, subtle blue accent. */
    val SwiftKey: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFFF1F3F5.toInt(),
        backgroundEndColor = 0xFFE9ECEF.toInt(),
        keyColor = 0xFFFFFFFF.toInt(),
        specialKeyColor = 0xFFE2E6EA.toInt(),
        keyBorderColor = 0x00000000,
        keyShadowColor = 0x1A000000,
        pressedKeyColor = 0xFFDDE3EA.toInt(),
        pressedKeyBorderColor = 0x00000000,
        activatedKeyColor = 0xFF0284C7.toInt(),
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFF111827.toInt(),
        secondaryLabelColor = 0xFF6B7280.toInt(),
        accentColor = 0xFF0284C7.toInt(),
        keyCornerRadiusDp = 6f,
        keyBorderWidthDp = 0f,
        keyShadowOffsetDp = 1.0f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFF374151.toInt(),
        accentKeyColor = 0xFF0284C7.toInt(),
        accentLabelColor = 0xFFFFFFFF.toInt(),
        popupSurfaceColor = 0xFFFFFFFF.toInt(),
        suggestionHighlightColor = 0xFF0284C7.toInt(),
        pressedKeyScale = 1.04f,
        suggestionDividerColor = 0x1A000000,
        popupShadowColor = 0x2A000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
    )

    /** Light floral preset: a blush-to-lilac gradient under near-white keycaps. */
    val Blossom: KeyboardTheme = BlossomKeyboardTheme

    /** Dark floral preset: an orchid-to-midnight gradient under translucent plates. */
    val Orchid: KeyboardTheme = OrchidKeyboardTheme
}
