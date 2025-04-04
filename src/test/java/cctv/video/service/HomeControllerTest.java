package cctv.video.service;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import io.micronaut.function.aws.proxy.payload2.APIGatewayV2HTTPEventFunction;
import io.micronaut.function.aws.proxy.MockLambdaContext;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class HomeControllerTest {
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
    void testHandler(ObjectMapper objectMapper) throws IOException {
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/")
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());
        System.out.println(objectMapper.writeValueAsString(request));
        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertEquals("{\"message\":\"Hello World\"}",  response.getBody());
    }
}
