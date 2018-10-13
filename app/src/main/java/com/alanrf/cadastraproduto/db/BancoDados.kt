package com.alanrf.cadastraproduto.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.alanrf.cadastraproduto.db.dao.ProdutoDao
import com.alanrf.cadastraproduto.db.entity.Produto
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

const val DATABASE_VERSION = 2

@Database(entities = [Produto::class], version = DATABASE_VERSION, exportSchema = false)
@TypeConverters(DateConverters::class)
abstract class BancoDados : RoomDatabase() {

    abstract fun produtoDao() : ProdutoDao
}

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE produto ADD COLUMN categoria TEXT NOT NULL DEFAULT 'Medicamento' ")
    }
}
