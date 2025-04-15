package cctv.video.service;

import cctv.video.service.domain.Video;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import io.micronaut.function.aws.proxy.MockLambdaContext;
import io.micronaut.function.aws.proxy.payload2.APIGatewayV2HTTPEventFunction;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class VideoControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoControllerTest.class);
    private static APIGatewayV2HTTPEventFunction handler;

    @BeforeAll
    static void setupSpec() {
        handler = new APIGatewayV2HTTPEventFunction();
    }
    @AfterAll
    static void cleanupSpec() {
        handler.getApplicationContext().close();
    }

    @Test
    void testHandler() {
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/")
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());

        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertEquals("{\"message\":\"Hello World\"}",  response.getBody());
    }

    @Test
    void testVideoGet(ObjectMapper objectMapper) throws IOException {
        UUID uuid = UUID.fromString("d02a9b70-75c0-4f26-9054-5f3cea0d869c");

        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/" + uuid)
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());



        HashMap<String, String> pathParameters = new HashMap<>();
        pathParameters.put("videoId", uuid.toString());
        request.setPathParameters(pathParameters);

        LOGGER.info("Sending testVideoGet request {}", request);
        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertTrue(response.getBody().contains("2025-04-09T16:06:48.085666"));

        Video video = objectMapper.readValue(response.getBody(), Video.class);
        LOGGER.info("Received testVideoGet response {}", objectMapper.writeValueAsString(video));
    }

    @Test
    void testVideoPost(ObjectMapper objectMapper) throws IOException {
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video")
                        .withMethod(HttpMethod.POST.toString())
                        .build()
                ).build());

        Video video = new Video(UUID.randomUUID(), "Test Video", "A Test Video.", List.of("testTag1", "testTag2"), LocalDateTime.now(), "John Fortnite");
        request.setBody(objectMapper.writeValueAsString(video));
        LOGGER.info("Sending testVideoPost request {}", request);

        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertEquals(HttpStatus.CREATED.name(),  response.getBody());
    }

    @Test
    void testVideosGet(ObjectMapper objectMapper) throws IOException {
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/videos")
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());

        LOGGER.info("Sending testVideosGet request");

        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());

        var videosArray = objectMapper.readValue(response.getBody(), Video[].class);
        Set<Video> videos = Set.of(videosArray);
        LOGGER.info("Received response from getVideos: {}", videos);
        assertTrue(videos.size() > 4);
    }
}
