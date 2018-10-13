package com.alanrf.cadastraproduto.model

import java.util.*

data class ProdutoFiltro (
        var nome: String?,
        var descricao: String?,
        var categoria: String?,
        var quantidade: Int?,
        var validade: Date?
)
