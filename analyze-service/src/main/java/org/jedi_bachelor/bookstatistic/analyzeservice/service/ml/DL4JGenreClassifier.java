package org.jedi_bachelor.bookstatistic.analyzeservice.service.ml;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class DL4JGenreClassifier implements GenreClassifier {
    private final EmbeddingService embeddingService;
    private MultiLayerNetwork network;
    private final String modelPath;

    // Твои жанры и поджанры
    private static final String[] GENRES = {
            "fantasy", "science_fiction", "romance", "detective",
            "thriller", "horror", "adventure", "historical", "mystery", "drama"
    };

    private static final String[] SUBGENRES = {
            "epic_fantasy", "dark_fantasy", "urban_fantasy", "heroic_fantasy",
            "cyberpunk", "space_opera", "dystopia", "post_apocalyptic",
            "noir", "cozy_mystery", "police_procedural", "psychological_thriller",
            "romantic_comedy", "dramatic_romance", "paranormal_romance",
            "bildungsroman", "moral_dilemma", "plot_twists"
    };

    private static final String[] ALL_LABELS = combineLabels();

    public DL4JGenreClassifier(
            EmbeddingService embeddingService,
            @Value("${ml.classifier.path:models/genre_classifier_dl4j.zip}") String modelPath) {
        this.embeddingService = embeddingService;
        this.modelPath = modelPath;
    }

    @PostConstruct
    public void init() {
        File modelFile = new File(modelPath);
        if (modelFile.exists()) {
            try {
                this.network = MultiLayerNetwork.load(modelFile, true);
                log.info("Loaded existing classifier from: {}", modelPath);
            } catch (IOException e) {
                log.error("Failed to load model, creating new one", e);
                createNewNetwork();
            }
        } else {
            log.info("No existing model found, creating new network");
            createNewNetwork();
        }
    }

    public void fit(DataSet dataset) {
        network.fit(dataset);
    }

    public double calculateLoss(DataSet dataset) {
        INDArray output = network.output(dataset.getFeatures());
        return dataset.getLabels().distance2(output);
    }

    private void createNewNetwork() {
        int inputSize = embeddingService.getEmbeddingSize(); // 384
        int outputSize = ALL_LABELS.length; // 28

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(42)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam(0.001))
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(inputSize)
                        .nOut(256)
                        .activation(Activation.RELU)
                        .dropOut(0.3)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(256)
                        .nOut(128)
                        .activation(Activation.RELU)
                        .dropOut(0.2)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.XENT)
                        .nIn(128)
                        .nOut(outputSize)
                        .activation(Activation.SIGMOID)
                        .build())
                .build();

        this.network = new MultiLayerNetwork(conf);
        this.network.init();

        log.info("Created new network: {} inputs, {} outputs", inputSize, outputSize);
    }

    @Override
    public Map<String, Double> predict(INDArray embedding) {
        INDArray output = network.output(embedding);

        Map<String, Double> predictions = new LinkedHashMap<>();
        for (int i = 0; i < ALL_LABELS.length; i++) {
            predictions.put(ALL_LABELS[i], output.getDouble(0, i));
        }

        return predictions;
    }

    @Override
    public Map<String, Double> predict(String text) {
        INDArray embedding = embeddingService.getEmbedding(text);
        return predict(embedding);
    }

    @Override
    public void fineTune(INDArray embedding, Map<String, Double> correctGenres) {
        // Создаем target вектор
        INDArray target = Nd4j.zeros(1, ALL_LABELS.length);
        for (int i = 0; i < ALL_LABELS.length; i++) {
            String label = ALL_LABELS[i];
            if (correctGenres.containsKey(label)) {
                target.putScalar(0, i, correctGenres.get(label));
            }
        }

        // Один шаг обучения
        DataSet example = new DataSet(embedding, target);
        network.fit(example);
    }

    @Override
    public void saveModel(String path) {
        try {
            network.save(new File(path));
            log.info("Model saved to: {}", path);
        } catch (IOException e) {
            log.error("Failed to save model", e);
            throw new RuntimeException("Cannot save model", e);
        }
    }

    @Override
    public void loadModel(String path) {
        try {
            this.network = MultiLayerNetwork.load(new File(path), true);
            log.info("Model loaded from: {}", path);
        } catch (IOException e) {
            log.error("Failed to load model", e);
            throw new RuntimeException("Cannot load model", e);
        }
    }

    @Override
    public String[] getGenreLabels() {
        return ALL_LABELS;
    }

    private static String[] combineLabels() {
        String[] all = new String[GENRES.length + SUBGENRES.length];
        System.arraycopy(GENRES, 0, all, 0, GENRES.length);
        System.arraycopy(SUBGENRES, 0, all, GENRES.length, SUBGENRES.length);
        return all;
    }
}
