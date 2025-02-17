package com.banyumas.wisata.utils

import android.content.Context
import com.banyumas.wisata.model.UiDestination
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object EmbeddingLoader {
    fun loadEmbeddingMap(
        context: Context,
        fileName: String = "word2vec.json"
    ): Map<String, FloatArray> {
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val type = object : TypeToken<Map<String, List<Float>>>() {}.type
        val rawMap: Map<String, List<Float>> = Gson().fromJson(jsonString, type)

        // Konversi List<Float> ke FloatArray untuk efisiensi
        return rawMap.mapValues { (_, v) -> v.toFloatArray() }
    }
}

object EmbeddingProcessor {
    fun getDestinationEmbedding(
        destination: UiDestination,
        embeddingMap: Map<String, FloatArray>
    ): FloatArray {
        val reviewsText = buildString {
            destination.destination.reviewsFromGoogle.forEach { append(it.text).append(" ") }
            destination.destination.reviewsFromLocal.forEach { append(it.text).append(" ") }
        }

        // Tokenisasi sederhana
        val tokens = reviewsText
            .lowercase()
            .replace("[^a-z0-9\\s]".toRegex(), "") // Hapus tanda baca
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }

        val firstVectorSize = embeddingMap.values.firstOrNull()?.size ?: return FloatArray(0)
        val sumVector = FloatArray(firstVectorSize) { 0f }
        var count = 0

        for (token in tokens) {
            val vector = embeddingMap[token] ?: continue  // Skip jika kata tidak ditemukan
            for (i in sumVector.indices) {
                sumVector[i] += vector[i]
            }
            count++
        }

        if (count > 0) {
            for (i in sumVector.indices) {
                sumVector[i] = sumVector[i] / count
            }
        }
        return sumVector
    }
}

object SimilarityCalculator {
    fun cosineSimilarity(vec1: FloatArray, vec2: FloatArray): Float {
        if (vec1.isEmpty() || vec2.isEmpty() || vec1.size != vec2.size) return 0f

        var dot = 0f
        var normA = 0f
        var normB = 0f

        for (i in vec1.indices) {
            dot += vec1[i] * vec2[i]
            normA += vec1[i] * vec1[i]
            normB += vec2[i] * vec2[i]
        }

        val denom =
            (kotlin.math.sqrt(normA.toDouble()) * kotlin.math.sqrt(normB.toDouble())).toFloat()
        return if (denom == 0f) 0f else dot / denom
    }
}