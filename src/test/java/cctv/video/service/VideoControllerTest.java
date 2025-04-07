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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        LOGGER.info("Sending test request {}", request);

        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertEquals(HttpStatus.CREATED.name(),  response.getBody());
    }
}
