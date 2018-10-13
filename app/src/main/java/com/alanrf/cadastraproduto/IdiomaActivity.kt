package com.alanrf.cadastraproduto

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.alanrf.cadastraproduto.helper.IdiomaHelper
import kotlinx.android.synthetic.main.activity_idioma.*

class IdiomaActivity : AppCompatActivity() {

    private val pt_BR = "pt"
    private val en_US = "en"
    private val es_ES = "es"
    private val de_DE = "de"

    lateinit var idiomaHelper: IdiomaHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idioma)

        idiomaHelper = IdiomaHelper(this, resources)
        inicializarIdioma();

        btAlterarIdioma.setOnClickListener {
            idiomaHelper.setIdioma(idiomaSelecionado());
            finish()
        }
    }

    fun idiomaSelecionado(): String {
        if (rb_en.isChecked) {
            return en_US
        }
        if (rb_es.isChecked) {
            return es_ES
        }
        if (rb_de.isChecked) {
            return de_DE
        }
        return pt_BR
    }

    fun inicializarIdioma() {
        val preferencia = idiomaHelper.getIdioma()
        if (en_US.equals(preferencia)) {
            rb_en.isChecked = true
        }
        if (es_ES.equals(preferencia)) {
            rb_es.isChecked = true
        }
        if (pt_BR.equals(preferencia)) {
            rb_pt.isChecked = true
        }
        if (de_DE.equals(preferencia)) {
            rb_de.isChecked = true
        }
    }
}
