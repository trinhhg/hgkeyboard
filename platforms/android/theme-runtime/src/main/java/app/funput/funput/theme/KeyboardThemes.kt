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

    /** Laban Key light preset (100% faithful to lxx_light + Vivo iQOO Neo 10 capture). */
    val Laban: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFFECEFF1.toInt(),
        backgroundEndColor = 0xFFECEFF1.toInt(),
        keyColor = 0xFFFFFFFF.toInt(),
        specialKeyColor = 0x00000000,
        keyBorderColor = 0x00000000,
        keyShadowColor = 0x2237474F,
        pressedKeyColor = 0xFFD9EAFB.toInt(),
        pressedKeyBorderColor = 0x00000000,
        activatedKeyColor = 0x2637474F,
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFF37474F.toInt(),
        secondaryLabelColor = 0x9937474F.toInt(),
        accentColor = 0xFF2095F4.toInt(),
        keyCornerRadiusDp = 4f,
        keyBorderWidthDp = 0f,
        keyShadowOffsetDp = 1.0f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFF37474F.toInt(),
        accentKeyColor = 0x00000000,
        accentLabelColor = 0xFF1976D2.toInt(),
        popupSurfaceColor = 0xFFFFFFFF.toInt(),
        suggestionHighlightColor = 0xFF37474F.toInt(),
        pressedKeyScale = 1.0f,
        suggestionDividerColor = 0x1F37474F,
        popupShadowColor = 0x2E000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
        enterUsesAccentPlate = false,
        spacebarOutlinedAccent = true,
        centerSecondaryLabel = false,
        toolbarBackgroundColor = 0xFFE5E8EC.toInt(),
    )

    /** Laban Key dark preset (lxx_dark tokens: plateless functional keys, blue icon Enter). */
    val LabanDark: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFF1F2227.toInt(),
        backgroundEndColor = 0xFF1F2227.toInt(),
        keyColor = 0xFF32363E.toInt(),
        specialKeyColor = 0x00000000,
        keyBorderColor = 0x00000000,
        keyShadowColor = 0x33000000,
        pressedKeyColor = 0xFF3D434D.toInt(),
        pressedKeyBorderColor = 0x00000000,
        activatedKeyColor = 0x29FFFFFF,
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xE6FFFFFF.toInt(),
        secondaryLabelColor = 0x80FFFFFF.toInt(),
        accentColor = 0xFF2095F4.toInt(),
        keyCornerRadiusDp = 4f,
        keyBorderWidthDp = 0f,
        keyShadowOffsetDp = 1.0f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xCCFFFFFF.toInt(),
        accentKeyColor = 0x00000000,
        accentLabelColor = 0xFF64B5F6.toInt(),
        popupSurfaceColor = 0xFF3A3F48.toInt(),
        suggestionHighlightColor = 0xFF64B5F6.toInt(),
        pressedKeyScale = 1.0f,
        suggestionDividerColor = 0x24FFFFFF,
        popupShadowColor = 0x55000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
        enterUsesAccentPlate = false,
        spacebarOutlinedAccent = true,
        centerSecondaryLabel = false,
        toolbarBackgroundColor = 0xFF1B1D22.toInt(),
    )

    /** Microsoft SwiftKey Fluent Adaptive Light (7c610bde: near-white function keys, neutral Enter). */
    val SwiftKey: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFFF1F1F1.toInt(),
        backgroundEndColor = 0xFFF1F1F1.toInt(),
        keyColor = 0xFFFFFFFF.toInt(),
        specialKeyColor = 0xFFFAFAFA.toInt(),
        keyBorderColor = 0x16000000,
        keyShadowColor = 0x18000000,
        pressedKeyColor = 0xFFE3E3E3.toInt(),
        pressedKeyBorderColor = 0x22000000,
        activatedKeyColor = 0xFFE3E3E3.toInt(),
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFF292929.toInt(),
        secondaryLabelColor = 0xFFB7B7B7.toInt(),
        accentColor = 0xFF3961ED.toInt(),
        keyCornerRadiusDp = 6f,
        keyBorderWidthDp = 0.45f,
        keyShadowOffsetDp = 0.8f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFF292929.toInt(),
        accentKeyColor = 0xFFFAFAFA.toInt(),
        accentLabelColor = 0xFF292929.toInt(),
        popupSurfaceColor = 0xFFE3E3E3.toInt(),
        suggestionHighlightColor = 0xFF292929.toInt(),
        pressedKeyScale = 1.0f,
        suggestionDividerColor = 0x1A000000,
        popupShadowColor = 0x26000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
        enterUsesAccentPlate = false,
        spacebarOutlinedAccent = false,
        centerSecondaryLabel = true,
        toolbarBackgroundColor = 0xFFF9F9F9.toInt(),
    )

    /** Microsoft SwiftKey Fluent Adaptive Dark (7c610bde dark tokens). */
    val SwiftKeyDark: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFF141414.toInt(),
        backgroundEndColor = 0xFF141414.toInt(),
        keyColor = 0xFF292929.toInt(),
        specialKeyColor = 0xFF181818.toInt(),
        keyBorderColor = 0x14FFFFFF,
        keyShadowColor = 0x2E000000,
        pressedKeyColor = 0xFF212121.toInt(),
        pressedKeyBorderColor = 0x26FFFFFF,
        activatedKeyColor = 0xFF363636.toInt(),
        activatedKeyBorderColor = 0x00000000,
        labelColor = 0xFFFFFFFF.toInt(),
        secondaryLabelColor = 0xFF777777.toInt(),
        accentColor = 0xFF9DC3FF.toInt(),
        keyCornerRadiusDp = 6f,
        keyBorderWidthDp = 0.45f,
        keyShadowOffsetDp = 0.8f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFFFFFFFF.toInt(),
        accentKeyColor = 0xFF181818.toInt(),
        accentLabelColor = 0xFFFFFFFF.toInt(),
        popupSurfaceColor = 0xFF404040.toInt(),
        suggestionHighlightColor = 0xFFFFFFFF.toInt(),
        pressedKeyScale = 1.0f,
        suggestionDividerColor = 0x22FFFFFF,
        popupShadowColor = 0x55000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
        enterUsesAccentPlate = false,
        spacebarOutlinedAccent = false,
        centerSecondaryLabel = true,
        toolbarBackgroundColor = 0xFF212121.toInt(),
    )

    /** Microsoft SwiftKey Classic Light (43ffed79: 5dp rounded keys with crisp 1dp #CCCCCC border). */
    val SwiftKeyClassic: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFFECEEF1.toInt(),
        backgroundEndColor = 0xFFECEEF1.toInt(),
        keyColor = 0xFFFFFFFF.toInt(),
        specialKeyColor = 0xFFEFF1F4.toInt(),
        keyBorderColor = 0xFFCCCCCC.toInt(),
        keyShadowColor = 0x16000000,
        pressedKeyColor = 0xFFE6E8EB.toInt(),
        pressedKeyBorderColor = 0xFFBBBBBB.toInt(),
        activatedKeyColor = 0xFFE1E3E6.toInt(),
        activatedKeyBorderColor = 0xFFBBBBBB.toInt(),
        labelColor = 0xFF000000.toInt(),
        secondaryLabelColor = 0xFFA8A8A8.toInt(),
        accentColor = 0xFF3961ED.toInt(),
        keyCornerRadiusDp = 5f,
        keyBorderWidthDp = 0.9f,
        keyShadowOffsetDp = 0.6f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFF252626.toInt(),
        accentKeyColor = 0xFFEFF1F4.toInt(),
        accentLabelColor = 0xFF252626.toInt(),
        popupSurfaceColor = 0xFFF5F7FB.toInt(),
        suggestionHighlightColor = 0xFF000000.toInt(),
        pressedKeyScale = 1.0f,
        suggestionDividerColor = 0x20000000,
        popupShadowColor = 0x28000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
        enterUsesAccentPlate = false,
        spacebarOutlinedAccent = false,
        centerSecondaryLabel = true,
        toolbarBackgroundColor = 0xFFECEEF1.toInt(),
    )

    /** Microsoft SwiftKey Classic Dark (c9161612: bordered dark keys). */
    val SwiftKeyClassicDark: KeyboardTheme = KeyboardTheme(
        backgroundStartColor = 0xFF1C1F27.toInt(),
        backgroundEndColor = 0xFF1C1F27.toInt(),
        keyColor = 0xFF393A41.toInt(),
        specialKeyColor = 0xFF272A31.toInt(),
        keyBorderColor = 0xFF000000.toInt(),
        keyShadowColor = 0x33000000,
        pressedKeyColor = 0xFF222226.toInt(),
        pressedKeyBorderColor = 0xFF111111.toInt(),
        activatedKeyColor = 0xFF454750.toInt(),
        activatedKeyBorderColor = 0xFF000000.toInt(),
        labelColor = 0xFFFFFFFF.toInt(),
        secondaryLabelColor = 0xFF707073.toInt(),
        accentColor = 0xFF7CAEFF.toInt(),
        keyCornerRadiusDp = 5f,
        keyBorderWidthDp = 0.8f,
        keyShadowOffsetDp = 0.6f,
        pressedKeyShadowOffsetDp = 0f,
        specialLabelColor = 0xFFDCDCDE.toInt(),
        accentKeyColor = 0xFF272A31.toInt(),
        accentLabelColor = 0xFFDCDCDE.toInt(),
        popupSurfaceColor = 0xFF393A41.toInt(),
        suggestionHighlightColor = 0xFFFFFFFF.toInt(),
        pressedKeyScale = 1.0f,
        suggestionDividerColor = 0x26FFFFFF,
        popupShadowColor = 0x55000000,
        keySurfaceStyle = KeyboardKeySurfaceStyle.FLAT,
        enterUsesAccentPlate = false,
        spacebarOutlinedAccent = false,
        centerSecondaryLabel = true,
        toolbarBackgroundColor = 0xFF1C1F27.toInt(),
    )

    /** Light floral preset: a blush-to-lilac gradient under near-white keycaps. */
    val Blossom: KeyboardTheme = BlossomKeyboardTheme

    /** Dark floral preset: an orchid-to-midnight gradient under translucent plates. */
    val Orchid: KeyboardTheme = OrchidKeyboardTheme
}
