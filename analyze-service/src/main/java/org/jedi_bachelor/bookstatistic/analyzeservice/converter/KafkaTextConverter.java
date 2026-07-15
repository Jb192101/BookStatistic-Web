package org.jedi_bachelor.bookstatistic.analyzeservice.converter;

import org.jedi_bachelor.bookstatistic.analyzeservice.inbox.entity.InboxAnalyzeEntity;
import org.jedi_bachelor.bookstatistic.commonslib.converter.Converter;
import org.jedi_bachelor.bookstatistic.commonslib.dto.kafka.KafkaTextAnalyzeDto;
import org.springframework.stereotype.Component;

@Component
public class KafkaTextConverter implements Converter<InboxAnalyzeEntity, KafkaTextAnalyzeDto> {
    @Override
    public InboxAnalyzeEntity convert(KafkaTextAnalyzeDto dto) {
        return null;
    }
}
