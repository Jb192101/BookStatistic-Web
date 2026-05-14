package org.jedi_bachelor.bookstatistic.bookservice.converter;

import org.jedi_bachelor.bookstatistic.bookservice.entity.Text;
import org.jedi_bachelor.bookstatistic.bookservice.redis.entity.TextFile;
import org.jedi_bachelor.bookstatistic.commonslib.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TextEntityConverter implements Converter<Text, TextFile> {
    /**
     * Метод конвертации TextFile в Text
     *
     * @param dto сущность из Redis TextFile
     * @return новую сущность Text в реляционной БД
     */
    @Override
    public Text convert(TextFile dto) {
        Text text = new Text();
        text.setTextFileRedisId(dto.getId());

        return text;
    }
}
