package com.example.ivalid_compose.repository

import com.example.ivalid_compose.ui.home.Category
import com.example.ivalid_compose.ui.home.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = db.collection("produtos").get().await()
            val list = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    android.util.Log.e("ProductRepository", "Erro ao mapear produto ${doc.id}: ${e.message}")
                    null
                }
            }
            android.util.Log.d("ProductRepository", "Produtos carregados: ${list.size}")
            list
        } catch (e: Exception) {
            android.util.Log.e("ProductRepository", "Erro geral produtos", e)
            emptyList()
        }
    }

    suspend fun getCategories(): List<Category> = runCatching {
        db.collection("categories").get().await().toObjects(Category::class.java)
    }.getOrDefault(emptyList())
}