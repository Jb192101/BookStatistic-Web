package org.jedi_bachelor.bookstatistic.analyzeservice.service.ml;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;

public interface EmbeddingService {
    /**
     * Получить эмбеддинг текста
     * @param text входной текст
     * @return вектор размерностью [1, 384]
     */
    INDArray getEmbedding(String text);

    /**
     * Получить эмбеддинги для батча текстов
     * @param texts список текстов
     * @return матрица [batch_size, 384]
     */
    INDArray getEmbeddings(List<String> texts);

    /**
     * Размерность эмбеддинга
     */
    int getEmbeddingSize();

    /**
     * Максимальная длина текста в токенах
     */
    int getMaxTokens();
}
