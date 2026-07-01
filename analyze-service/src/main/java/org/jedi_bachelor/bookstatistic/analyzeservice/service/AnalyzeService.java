package org.jedi_bachelor.bookstatistic.analyzeservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.configuration.InteractionPathsConfiguration;
import org.jedi_bachelor.bookstatistic.analyzeservice.report.UserReportDocument;
import org.jedi_bachelor.bookstatistic.analyzeservice.repository.BookAnalyzeResultRepository;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.ResponseDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyzeService {
    private final BookAnalyzeResultRepository bookAnalyzeResultRepository;

    private final InteractionClient bookInteractionClient;

    private final InteractionClient responseInteractionClient;

    private final InteractionClient accountInteractionClient;

    private final InteractionPathsConfiguration interactionPathsConfiguration;

    @Async("analyzeExecutor")
    public CompletableFuture<List<BookDto>> fetchBooksAsync(UUID userId) {
        return CompletableFuture.completedFuture(
                (List<BookDto>) this.bookInteractionClient.sendRequest(
                    HttpMethod.GET,
                    "/" + userId.toString()
            )
        );
    }

    @Async("analyzeExecutor")
    public CompletableFuture<List<ResponseDto>> fetchResponsesAsync(UUID userId) {
        return CompletableFuture.completedFuture(
                (List<ResponseDto>) this.responseInteractionClient.sendRequest(
                        HttpMethod.GET,
                        this.interactionPathsConfiguration.getResponseGetUsersResponsesUri()
                                + userId.toString()
                )
        );
    }

    @Async("analyzeExecutor")
    public CompletableFuture<UserDto> fetchAccountAsync(UUID userId) {
        return CompletableFuture.completedFuture(
                (UserDto) this.accountInteractionClient.sendRequest(
                        HttpMethod.GET,
                        "/" + userId.toString()
                )
        );
    }

    /**
     * Метод для анализа пользователя и составление о нём отчёта
     */
    public void analyzeUser(UUID userId) throws ExecutionException, InterruptedException {
        CompletableFuture<List<BookDto>> booksFuture = this.fetchBooksAsync(userId);
        CompletableFuture<UserDto> userFuture = this.fetchAccountAsync(userId);
        CompletableFuture<List<ResponseDto>> responseFuture = this.fetchResponsesAsync(userId);

        CompletableFuture.allOf(booksFuture, userFuture, responseFuture).join();

        List<BookDto> books = booksFuture.get();
        UserDto user = userFuture.get();
        List<ResponseDto> responses= responseFuture.get();

        // Формирование UserReportDocument-а
        UserReportDocument document = new UserReportDocument();
        document.setUserId(userId);
        document.setBirthday(user.birthDay());
        document.setUsername(user.username());
        document.setResidenceRegion(user.residenceCountry());

        List<UserReportDocument.BookStats> bookStats = new ArrayList<>();
        for(BookDto book : books) {
            UserReportDocument.BookStats stat = new UserReportDocument.BookStats();
            stat.setBookId(book.id());
            stat.setBookName(book.title());
            //stat.setReadedPercent(0);

            bookStats.add(stat);
        }

        document.setBookStatsList(bookStats);

        List<UserReportDocument.UserResponseStats> responseStats = new ArrayList<>();
        for(ResponseDto response : responses) {
            UserReportDocument.UserResponseStats stat = new UserReportDocument.UserResponseStats();
            stat.setBookId(response.bookId());
            stat.setStarsCount(response.stars());
            stat.setResponseText(response.responseText());

            responseStats.add(stat);
        }

        document.setUserResponseStatsList(responseStats);
    }
}
