package com.alanrf.cadastraproduto.db.dao

import android.arch.persistence.room.*
import com.alanrf.cadastraproduto.db.entity.Produto
import java.util.*

@Dao
interface ProdutoDao {

    @Query("SELECT * FROM produto")
    fun selecionarTodos(): List<Produto>

    @Query("SELECT * FROM produto WHERE id = :idParam")
    fun selecionarProduto(idParam: Int): Produto

    @Query("SELECT * FROM produto WHERE quantidade = :filter")
    fun selecionarProdutosPorQuantidade(filter: Int): List<Produto>

    @Query("SELECT * FROM produto WHERE categoria = :filter")
    fun selecionarProdutosPorCategoria(filter: String): List<Produto>

    @Query("SELECT * FROM produto WHERE nome like :filter")
    fun selecionarProdutosPorNome(filter: String): List<Produto>

    @Query("SELECT * FROM produto WHERE descricao like :filter")
    fun selecionarProdutosPorDescricao(filter: String): List<Produto>

    @Query("SELECT * FROM produto WHERE validade = :filter")
    fun selecionarProdutosPorDataValidade(filter: Date): List<Produto>

    @Insert
    fun inserir(vararg produto: Produto)

    @Delete
    fun remover(vararg produto: Produto)

    @Update
    fun atualizar(vararg produto: Produto)
}