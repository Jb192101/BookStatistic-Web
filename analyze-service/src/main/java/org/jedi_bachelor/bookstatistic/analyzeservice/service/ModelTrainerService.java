package org.jedi_bachelor.bookstatistic.analyzeservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.entity.TrainingData;
import org.jedi_bachelor.bookstatistic.analyzeservice.repository.TrainingDataRepository;
import org.jedi_bachelor.bookstatistic.analyzeservice.service.ml.DL4JGenreClassifier;
import org.jedi_bachelor.bookstatistic.analyzeservice.service.ml.ONNXEmbeddingService;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelTrainerService {
    private final TrainingDataRepository trainingDataRepository;

    private final ONNXEmbeddingService embeddingService;

    private final DL4JGenreClassifier classifier;

    private final ObjectMapper objectMapper;

    private static final int EPOCHS = 10;

    /**
     * Запустить тренировку модели на размеченных данных
     */
    @Async
    public void train() {
        log.info("Starting model training...");

        List<TrainingData> trainingData = this.trainingDataRepository.findByUsedForTrainingFalse();

        if (trainingData.isEmpty()) {
            log.warn("No training data found");
            return;
        }

        log.info("Found {} labeled books", trainingData.size());

        DataSet dataset = this.convertToDataSet(trainingData);

        if (dataset == null) {
            log.error("Failed to create dataset");
            return;
        }

        // Просто обучаем — learning rate уже в конфигурации модели
        for (int epoch = 0; epoch < EPOCHS; epoch++) {
            dataset.shuffle();
            this.classifier.fit(dataset);

            double loss = this.classifier.calculateLoss(dataset);
            log.info("Epoch {} completed, loss: {:.4f}", epoch + 1, loss);
        }

        // Сохраняем
        String modelPath = "models/genre_classifier_dl4j_trained.zip";
        this.classifier.saveModel(modelPath);

        log.info("Model saved to: {}", modelPath);

        // Отмечаем как использованные
        for (TrainingData data : trainingData) {
            data.setUsedForTraining(true);
        }

        this.trainingDataRepository.saveAll(trainingData);

        log.info("Training completed!");
    }

    /**
     * Преобразование размеченных данных в DataSet для DL4J
     */
    private DataSet convertToDataSet(List<TrainingData> trainingData) {
        List<INDArray> embeddings = new ArrayList<>();
        List<INDArray> targets = new ArrayList<>();

        String[] allLabels = this.classifier.getGenreLabels();

        for (TrainingData data : trainingData) {
            try {
                // Получаем эмбеддинг текста
                String text = data.getBookText();
                if (text == null || text.isBlank()) {
                    log.warn("Empty text for book: {}", data.getBookId());
                    continue;
                }

                INDArray embedding = this.embeddingService.getEmbedding(text);
                embeddings.add(embedding);

                // Парсим правильные жанры
                Map<String, Double> correctGenres = this.objectMapper.readValue(
                        data.getCorrectGenres(),
                        new TypeReference<>() {}
                );

                // Создаем целевой вектор
                INDArray target = Nd4j.zeros(1, allLabels.length);

                for (int i = 0; i < allLabels.length; i++) {
                    String label = allLabels[i];
                    double score = correctGenres.getOrDefault(label, 0.0);
                    target.putScalar(0, i, score);
                }

                targets.add(target);

            } catch (Exception e) {
                log.error("Failed to process training data: {}", data.getId(), e);
            }
        }

        if (embeddings.isEmpty()) {
            return null;
        }

        INDArray features = Nd4j.vstack(embeddings);
        INDArray labels = Nd4j.vstack(targets);

        return new DataSet(features, labels);
    }
}
