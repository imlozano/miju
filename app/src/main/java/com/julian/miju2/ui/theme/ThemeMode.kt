package com.julian.miju2.ui.theme

/**
 * Modo de tema elegido por el usuario. Se persiste en SharedPreferences con [key].
 * SYSTEM (por defecto) sigue el ajuste del sistema operativo.
 */
enum class ThemeMode(val key: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromKey(key: String?): ThemeMode =
            entries.firstOrNull { it.key == key } ?: SYSTEM
    }
}
