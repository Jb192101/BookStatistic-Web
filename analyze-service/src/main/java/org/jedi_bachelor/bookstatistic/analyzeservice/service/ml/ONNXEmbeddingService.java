package org.jedi_bachelor.bookstatistic.analyzeservice.service.ml;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ONNXEmbeddingService implements EmbeddingService {
    private final String modelPath;
    private final String vocabPath;
    private OrtEnvironment env;
    private OrtSession session;
    private BertTokenizer tokenizer;

    private static final int EMBEDDING_SIZE = 384;  // all-MiniLM-L6-v2
    private static final int MAX_TOKENS = 512;

    public ONNXEmbeddingService(
            @Value("${ml.model.path:models/sbert_base.onnx}") String modelPath,
            @Value("${ml.vocab.path:models/vocab.txt}") String vocabPath) {
        this.modelPath = modelPath;
        this.vocabPath = vocabPath;
    }

    @PostConstruct
    public void init() {
        try {
            this.env = OrtEnvironment.getEnvironment();

            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            options.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);
            options.setIntraOpNumThreads(Runtime.getRuntime().availableProcessors());

            this.session = env.createSession(modelPath, options);
            this.tokenizer = new BertTokenizer(vocabPath);

            log.info("ONNX model loaded successfully from: {}", modelPath);
            log.info("Embedding size: {}, Max tokens: {}", EMBEDDING_SIZE, MAX_TOKENS);
        } catch (OrtException e) {
            log.error("Failed to initialize ONNX model", e);
            throw new RuntimeException("Cannot load SBERT model", e);
        }
    }

    @Override
    public INDArray getEmbedding(String text) {
        try {
            // Токенизация
            BertTokenizer.TokenizationResult tokens = tokenizer.tokenize(text);

            // Создаем входные тензоры
            Map<String, OnnxTensor> inputs = new HashMap<>();

            long[] inputIds = tokens.getInputIds();
            long[] attentionMask = tokens.getAttentionMask();

            OnnxTensor inputIdsTensor = OnnxTensor.createTensor(
                    env, new long[][]{inputIds});
            OnnxTensor attentionMaskTensor = OnnxTensor.createTensor(
                    env, new long[][]{attentionMask});

            inputs.put("input_ids", inputIdsTensor);
            inputs.put("attention_mask", attentionMaskTensor);

            // Инференс
            OrtSession.Result result = session.run(inputs);

            // Получаем last_hidden_state [1, seq_len, 384]
            float[][][] hiddenStates = (float[][][]) result.get(0).getValue();

            // Mean pooling с учетом attention mask
            float[] embedding = meanPooling(hiddenStates[0], attentionMask);

            // L2 нормализация (как в оригинальном SBERT)
            l2Normalize(embedding);

            // Конвертируем в INDArray
            INDArray result_embedding = Nd4j.create(embedding).reshape(1, EMBEDDING_SIZE);

            // Очищаем ресурсы
            result.close();
            inputIdsTensor.close();
            attentionMaskTensor.close();

            return result_embedding;
        } catch (OrtException e) {
            log.error("Error getting embedding", e);
            throw new RuntimeException("Embedding inference failed", e);
        }
    }

    @Override
    public INDArray getEmbeddings(List<String> texts) {
        // Батчевая обработка для эффективности
        List<INDArray> embeddings = new ArrayList<>();
        for (String text : texts) {
            embeddings.add(getEmbedding(text));
        }
        return Nd4j.vstack(embeddings);
    }

    private float[] meanPooling(float[][] tokenEmbeddings, long[] attentionMask) {
        int seqLength = tokenEmbeddings.length;
        int hiddenSize = tokenEmbeddings[0].length;

        float[] pooled = new float[hiddenSize];
        float totalWeight = 0.0f;

        for (int i = 0; i < seqLength; i++) {
            float weight = attentionMask[i];
            if (weight > 0) {
                for (int j = 0; j < hiddenSize; j++) {
                    pooled[j] += tokenEmbeddings[i][j] * weight;
                }
                totalWeight += weight;
            }
        }

        if (totalWeight > 0) {
            for (int j = 0; j < hiddenSize; j++) {
                pooled[j] /= totalWeight;
            }
        }

        return pooled;
    }

    private void l2Normalize(float[] vector) {
        float sumSquares = 0.0f;
        for (float v : vector) {
            sumSquares += v * v;
        }
        float norm = (float) Math.sqrt(sumSquares);
        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
    }

    @Override
    public int getEmbeddingSize() {
        return EMBEDDING_SIZE;
    }

    @Override
    public int getMaxTokens() {
        return MAX_TOKENS;
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (session != null) session.close();
            if (env != null) env.close();
            log.info("ONNX resources cleaned up");
        } catch (OrtException e) {
            log.error("Error cleaning up ONNX resources", e);
        }
    }
}
