package org.jedi_bachelor.bookstatistic.analyzeservice.service.ml;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Map;

public interface GenreClassifier {
    /**
     * Предсказать жанры по эмбеддингу
     * @param embedding вектор [1, embeddingSize]
     * @return карта жанр -> вероятность
     */
    Map<String, Double> predict(INDArray embedding);

    /**
     * Предсказать жанры по тексту
     * @param text текст книги
     * @return карта жанр -> вероятность
     */
    Map<String, Double> predict(String text);

    /**
     * Дообучить на одном примере
     */
    void fineTune(INDArray embedding, Map<String, Double> correctGenres);

    /**
     * Сохранить модель
     */
    void saveModel(String path);

    /**
     * Загрузить модель
     */
    void loadModel(String path);

    /**
     * Получить список всех жанров
     */
    String[] getGenreLabels();
}
