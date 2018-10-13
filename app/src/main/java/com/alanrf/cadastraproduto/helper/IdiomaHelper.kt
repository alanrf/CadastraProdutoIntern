package com.alanrf.cadastraproduto.helper

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*

class IdiomaHelper(context: Context, resources: Resources) {
    // Shared Preferences Config
    private val PREF_NAME = "ProdutosPreferencia"
    private val KEY_LANGUAGE = "Idioma"

    var context = context
    var pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setIdioma(idioma: String) {
        val editor = pref.edit()
        editor.putString(KEY_LANGUAGE, idioma)
        editor.apply()
        editor.commit()

        alteraIdioma(idioma)
    }

    fun alteraIdioma(idioma: String) {
        val locale = Locale(idioma)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.getResources().updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getIdioma(): String {
        val idioma = pref.getString(KEY_LANGUAGE, "")
        return idioma
    }

    fun carregaIdioma() {
        val idioma = getIdioma()
        if (idioma.isNotEmpty()) {
            alteraIdioma(idioma)
        }
    }
}