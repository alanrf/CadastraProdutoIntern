package com.alanrf.cadastraproduto

import android.app.DatePickerDialog
import android.arch.persistence.room.Room
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import com.alanrf.cadastraproduto.MainActivity.Companion.meusProdutosArrayList
import com.alanrf.cadastraproduto.MainActivity.Companion.produtoAdapter
import com.alanrf.cadastraproduto.MainActivity.Companion.produtoDao
import com.alanrf.cadastraproduto.R.id.rcv_lista_produtos
import com.alanrf.cadastraproduto.db.BancoDados
import com.alanrf.cadastraproduto.db.MIGRATION_1_2
import com.alanrf.cadastraproduto.db.dao.ProdutoDao
import com.alanrf.cadastraproduto.db.entity.Produto
import com.alanrf.cadastraproduto.model.ProdutoFiltro
import com.alanrf.cadastraproduto.swipehelper.SwipeToDeleteCallback
import com.alanrf.cadastraproduto.swipehelper.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.dialog_filtro.view.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val myFormat = "dd/MM/yyyy"
    private val sdf = SimpleDateFormat(myFormat)

    companion object {
        internal val nomeBancoDados: String = "nomebancodedados"
        internal lateinit var produtoDao: ProdutoDao
        internal lateinit var meusProdutosArrayList: ArrayList<Produto>
        internal lateinit var produtoAdapter: ProdutoListaAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        configuraBanco()
        configurarComportamentoListaRecyclerView(meusProdutosArrayList)
        configuraButtons()
    }

    override fun onResume() {
        super.onResume()
        produtoAdapter.substituirTodosProdutos(produtoDao.selecionarTodos())
    }

    private fun configuraButtons() {
        fabNovo.setOnClickListener { _ ->
            val produtoCadastroIntent = Intent(this, CadastroActivity::class.java)
            startActivity(produtoCadastroIntent)
        }

        fabFiltro.setOnClickListener { _ ->
            val mBuilder = AlertDialog.Builder(this@MainActivity)
            val mView = layoutInflater.inflate(R.layout.dialog_filtro, null)
            mBuilder.setView(mView)
            val dialog = mBuilder.create()
            dialog.show()

            var produtoFiltro : ProdutoFiltro

            val btFiltrarPor = mView.btDialogFiltrar
            val btCancelarFiltro = mView.btDialogCancelar
            val btLimparFiltro = mView.btDialogLimpar;
            val edFiltroTexto = mView.edDialogFiltroTexto
            val edFiltroQuantidade = mView.edDialogFiltroQuantidade
            val edFiltroData = mView.edDialogFiltroData
            val spFiltroCategoria = mView.spDialogFiltroCategoria
            val spFiltro = mView.spDialogFiltro

            var cal = Calendar.getInstance()

            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat)

                val str = sdf.format(cal.time)
                edFiltroData.setText(str)

            }

            edFiltroData.inputType = InputType.TYPE_NULL;
            edFiltroData.setOnClickListener {
                DatePickerDialog(this@MainActivity, dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
            }


            spFiltro.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    val selected = spFiltro.selectedItem.toString()
                    if (resources.getString(R.string.categoria) == selected) {
                        esconderCampo(edFiltroData)
                        esconderCampo(edFiltroQuantidade)
                        esconderCampo(edFiltroTexto)
                        spFiltroCategoria.visibility = View.VISIBLE
                    }

                    if (resources.getString(R.string.nome) == selected || resources.getString(R.string.descricao) == selected) {
                        esconderCampo(edFiltroData)
                        esconderCampo(edFiltroQuantidade)
                        esconderSpinner(spFiltroCategoria)
                        edFiltroTexto.visibility = View.VISIBLE
                    }

                    if (resources.getString(R.string.quantidade) == selected) {
                        esconderCampo(edFiltroData)
                        esconderCampo(edFiltroTexto)
                        esconderSpinner(spFiltroCategoria)
                        edFiltroQuantidade.visibility = View.VISIBLE
                    }

                    if (resources.getString(R.string.datavalidade) == spFiltro.selectedItem.toString()) {
                        esconderCampo(edFiltroTexto)
                        esconderCampo(edFiltroQuantidade)
                        esconderSpinner(spFiltroCategoria)
                        edFiltroData.visibility = View.VISIBLE
                    }
                }
            })

            //Definir os campos visiveis no primeiro acesso (filtro por Nome)
            esconderCampo(edFiltroData)
            esconderCampo(edFiltroQuantidade)
            esconderSpinner(spFiltroCategoria)
            edFiltroTexto.visibility = View.VISIBLE

            btFiltrarPor.setOnClickListener(View.OnClickListener {
                produtoFiltro = criarEntidadeFiltro(spFiltro.selectedItem.toString(), spFiltroCategoria, edFiltroTexto, edFiltroQuantidade, edFiltroData)
                produtoAdapter.substituirTodosProdutos(carregarProdutosPorExemplo(produtoFiltro))
                dialog.dismiss();
            })
            btCancelarFiltro.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
            })
            btLimparFiltro.setOnClickListener(View.OnClickListener {
                produtoAdapter.substituirTodosProdutos(carregarProdutos())
                dialog.dismiss();
            })

        }
    }

    private fun esconderSpinner(spFiltroCategoria: Spinner) {
        spFiltroCategoria.visibility = View.GONE
        spFiltroCategoria.setSelection(0)
    }

    private fun esconderCampo(ed: EditText) {
        ed.visibility = View.GONE
        ed.text.clear();
    }

    private fun criarEntidadeFiltro(selected: String, spFiltroCategoria: Spinner, edFiltroTexto: EditText, edFiltroQuantidade: EditText, edFiltroData: EditText): ProdutoFiltro {
        var produtoFiltro = ProdutoFiltro(categoria = null, nome = null, descricao = null, validade = null, quantidade = null)

        if (resources.getString(R.string.categoria) == selected)
            produtoFiltro.categoria = spFiltroCategoria.selectedItem.toString()

        if (resources.getString(R.string.nome) == selected)
            produtoFiltro.nome = edFiltroTexto.text.toString()

        if (resources.getString(R.string.descricao) == selected)
            produtoFiltro.descricao = edFiltroTexto.text.toString()

        if (resources.getString(R.string.quantidade) == selected)
            produtoFiltro.quantidade = edFiltroQuantidade.text.toString().toInt()

        if (resources.getString(R.string.datavalidade) == selected)
            produtoFiltro.validade = sdf.parse(edFiltroData.text.toString())

        return produtoFiltro
    }

    private fun configuraBanco() {
        val db = Room.databaseBuilder(this,
                BancoDados::class.java,
                Companion.nomeBancoDados).addMigrations(MIGRATION_1_2).allowMainThreadQueries().build()

        produtoDao = db.produtoDao()
        meusProdutosArrayList = carregarProdutos()
    }


    private fun configurarComportamentoListaRecyclerView(meusProdutosArrayList: ArrayList<Produto>) {
        rcv_lista_produtos.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        produtoAdapter = ProdutoListaAdapter(meusProdutosArrayList, context = this)
        rcv_lista_produtos.adapter = produtoAdapter

        val itemTouchHelperDelete = ItemTouchHelper(
                object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        var posicao = viewHolder.adapterPosition
                        val prod = meusProdutosArrayList.get(posicao)
                        produtoDao.remover(prod)
                        meusProdutosArrayList.removeAt(posicao)
                        produtoAdapter.notifyItemRemoved(posicao)
                    }
                }
        )
        itemTouchHelperDelete.attachToRecyclerView(rcv_lista_produtos)

        val produtoCadastroIntent = Intent(this, CadastroActivity::class.java)
        val itemTouchHelperEdit = ItemTouchHelper(
                object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        var posicao = viewHolder.adapterPosition
                        val prod = meusProdutosArrayList.get(posicao)
                        produtoCadastroIntent.putExtra("produto", prod)

                        startActivity(produtoCadastroIntent)
                    }
                }
        )
        itemTouchHelperEdit.attachToRecyclerView(rcv_lista_produtos)
    }

    private fun carregarProdutos(): ArrayList<Produto> {
        return produtoDao.selecionarTodos() as ArrayList<Produto>
    }

    private fun carregarProdutosPorExemplo(produtoFiltro: ProdutoFiltro): ArrayList<Produto> {
        if (produtoFiltro.categoria != null) {
            return produtoDao.selecionarProdutosPorCategoria(produtoFiltro.categoria!!) as ArrayList<Produto>;
        }
        if (produtoFiltro.nome != null) {
            return produtoDao.selecionarProdutosPorNome(likeFilter(produtoFiltro.nome!!)) as ArrayList<Produto>;
        }
        if (produtoFiltro.descricao != null) {
            return produtoDao.selecionarProdutosPorDescricao(likeFilter(produtoFiltro.descricao!!)) as ArrayList<Produto>;
        }
        if (produtoFiltro.quantidade != null) {
            return produtoDao.selecionarProdutosPorQuantidade(produtoFiltro.quantidade!!) as ArrayList<Produto>;
        }
        if (produtoFiltro.validade != null) {
            return produtoDao.selecionarProdutosPorDataValidade(produtoFiltro.validade!!) as ArrayList<Produto>;
        }
        return ArrayList<Produto>()
    }

    private fun likeFilter(s: String) : String {
        return "%" + s +"%"
    }
}
