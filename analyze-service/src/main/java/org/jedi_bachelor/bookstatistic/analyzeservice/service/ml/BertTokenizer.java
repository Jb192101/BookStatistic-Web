package org.jedi_bachelor.bookstatistic.analyzeservice.service.ml;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BertTokenizer {
    private final Map<String, Integer> vocab = new HashMap<>();
    private final Map<Integer, String> idToToken = new HashMap<>();
    private static final int MAX_LENGTH = 512;
    private static final String CLS_TOKEN = "[CLS]";
    private static final String SEP_TOKEN = "[SEP]";
    private static final String PAD_TOKEN = "[PAD]";
    private static final String UNK_TOKEN = "[UNK]";

    public BertTokenizer(String vocabPath) {
        this.loadVocab(vocabPath);
    }

    private void loadVocab(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int id = 0;
            while ((line = reader.readLine()) != null) {
                String token = line.trim();
                vocab.put(token, id);
                idToToken.put(id, token);
                id++;
            }
            log.info("Loaded {} tokens from vocabulary", vocab.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load vocabulary from: " + path, e);
        }
    }

    public TokenizationResult tokenize(String text) {
        // Базовая токенизация (упрощённая)
        String[] words = text.toLowerCase().split("\\s+");

        List<Long> inputIds = new ArrayList<>();
        List<Long> attentionMask = new ArrayList<>();

        // [CLS] токен
        inputIds.add((long) vocab.getOrDefault(CLS_TOKEN, 101));
        attentionMask.add(1L);

        // Токенизируем слова
        for (String word : words) {
            if (inputIds.size() >= MAX_LENGTH - 1) break;

            // Ищем токен или используем [UNK]
            Integer tokenId = vocab.get(word);
            if (tokenId == null) {
                // Простая BPE-подобная токенизация для неизвестных слов
                tokenId = vocab.getOrDefault(UNK_TOKEN, 100);
            }

            inputIds.add((long) tokenId);
            attentionMask.add(1L);
        }

        // [SEP] токен
        if (inputIds.size() < MAX_LENGTH) {
            inputIds.add((long) vocab.getOrDefault(SEP_TOKEN, 102));
            attentionMask.add(1L);
        }

        // Паддинг до максимальной длины
        while (inputIds.size() < MAX_LENGTH) {
            inputIds.add((long) vocab.getOrDefault(PAD_TOKEN, 0));
            attentionMask.add(0L);
        }

        return new TokenizationResult(
                inputIds.stream().mapToLong(Long::longValue).toArray(),
                attentionMask.stream().mapToLong(Long::longValue).toArray()
        );
    }

    public static class TokenizationResult {
        private final long[] inputIds;
        private final long[] attentionMask;

        public TokenizationResult(long[] inputIds, long[] attentionMask) {
            this.inputIds = inputIds;
            this.attentionMask = attentionMask;
        }

        public long[] getInputIds() {
            return inputIds;
        }

        public long[] getAttentionMask() {
            return attentionMask;
        }
    }
}
