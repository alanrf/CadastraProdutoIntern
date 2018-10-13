package com.alanrf.cadastraproduto.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import java.io.Serializable
import java.util.*

@Entity
data class Produto (
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val nome: String,
        val descricao: String,
        val categoria: String,
        val quantidade: Int,
        val validade: Date = Date(),
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val imagem: ByteArray? = null
) : Serializable