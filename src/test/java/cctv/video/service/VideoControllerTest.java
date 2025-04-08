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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video")
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());

        UUID uuid = UUID.fromString("549abc29-46eb-4feb-b919-c2db7a4bc9a7");

        HashMap<String, String> pathParameters = new HashMap<>();
        pathParameters.put("video", uuid.toString());
        request.setPathParameters(pathParameters);

        LOGGER.info("Sending testVideoGet request {}", request);
        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertTrue(response.getBody().contains("Sat Apr 05 15:33:31 EDT 2025"));

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

        Video video = new Video(UUID.randomUUID(), "Test Video", "A Test Video.", List.of("testTag1", "testTag2"), (new Date()), "John Fortnite");
        request.setBody(objectMapper.writeValueAsString(video));
        LOGGER.info("Sending testVideoPost request {}", request);

        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertEquals(HttpStatus.CREATED.name(),  response.getBody());
    }
}
