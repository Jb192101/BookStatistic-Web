package org.jedi_bachelor.bookstatistic.bookservice.converter;

import org.jedi_bachelor.bookstatistic.bookservice.entity.Response;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Stars;
import org.jedi_bachelor.bookstatistic.commonslib.converter.Converter;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.ResponseCreateUpdateDto;
import org.springframework.stereotype.Component;

@Component
public class ResponseConverter implements Converter<Response, ResponseCreateUpdateDto> {
    @Override
    public Response convert(ResponseCreateUpdateDto dto) {
        Response response = new Response();
        response.setId(new Response.ResponseId(dto.userId(), dto.bookId()));
        response.setResponseText(dto.responseText());
        response.setStars(Stars.from(dto.countOfStars()));

        return response;
    }
}
