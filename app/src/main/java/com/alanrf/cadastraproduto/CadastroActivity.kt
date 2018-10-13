package com.alanrf.cadastraproduto

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.alanrf.cadastraproduto.MainActivity.Companion.meusProdutosArrayList
import com.alanrf.cadastraproduto.MainActivity.Companion.produtoDao
import com.alanrf.cadastraproduto.db.entity.Produto
import kotlinx.android.synthetic.main.content_cadastro.*
import java.text.SimpleDateFormat
import java.util.*


class CadastroActivity : AppCompatActivity() {

    private val myFormat = "dd/MM/yyyy"
    private val sdf = SimpleDateFormat(myFormat)
    private var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        defineComportamentoDataValidade()
    }

    override fun onResume() {
        super.onResume()

        if (intent.extras != null && intent.extras.containsKey("produto")) {
            val produtoOriginal = intent.extras.getSerializable("produto") as Produto

            edNome.editText?.setText(produtoOriginal.nome)
            edDescricao.editText?.setText(produtoOriginal.descricao)
            edQuantidade.editText?.setText(""+produtoOriginal.quantidade)
            edDataValidade.editText?.setText(sdf.format(produtoOriginal.validade))

            cal.timeInMillis = produtoOriginal.validade.time

            selectSpinnerItemByValue(spCategoria, produtoOriginal.categoria)
        }
    }

    private fun selectSpinnerItemByValue(sp: Spinner, value: String) {
        val adapter = sp.adapter as ArrayAdapter<String>
        for (position in 0 until adapter.getCount()) {
            if (adapter.getItem(position).toString() == value) {
                sp.setSelection(position)
                return
            }
        }
    }

    private fun defineComportamentoDataValidade() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val str = sdf.format(cal.time)
            edDataValidade.editText?.setText(str)
            edDataValidade.editText?.error = null
        }

        edDataValidade.editText?.inputType = InputType.TYPE_NULL;
        edDataValidade.editText?.showSoftInputOnFocus = false;
        edDataValidade.editText?.setOnClickListener {
            DatePickerDialog(this@CadastroActivity, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    fun gravar(view: View) {
        if (!validarCampos()) {
            return
        }

        val nome = edNome.editText?.text.toString()
        val descricao = edDescricao.editText?.text.toString()
        val quantidade = edQuantidade.editText?.text.toString().toInt()
        val dataValidade = sdf.parse(edDataValidade.editText?.text.toString())
        val categoria = spCategoria.selectedItem.toString()

        if (intent.extras != null && intent.extras.containsKey("produto")) {
            val produtoOriginal = intent.extras.getSerializable("produto") as Produto
            val produto = Produto(id = produtoOriginal.id, nome = nome, descricao = descricao, categoria = categoria, quantidade = quantidade, validade = dataValidade)
            produtoDao.atualizar(produto)
        } else {
            val produto = Produto(nome = nome, descricao = descricao, categoria = categoria, quantidade = quantidade, validade = dataValidade)
            produtoDao.inserir(produto)
            meusProdutosArrayList.add(produto)
        }

        finish()
    }

    private fun validarCampos() : Boolean {
        var b =  validaEditTextNotNull(edNome.editText)
        b = validaEditTextNotNull(edDescricao.editText) && b;
        b = validaEditTextNotNull(edQuantidade.editText) && b;
        b = validaEditTextNotNull(edDataValidade.editText) && b;
        return b
    }

    private fun validaEditTextNotNull(ed: EditText?): Boolean {
        if (ed == null) {
            Toast.makeText(this, "Um erro inesperado aconteceu, contate a empresa desenvolvedora", Toast.LENGTH_LONG).show()
        }

        if (ed?.text == null || ed?.text.toString().trim() == "") {
            ed?.error = "Este campo deve ser preenchido"
            return false
        }

        return true
    }
}
