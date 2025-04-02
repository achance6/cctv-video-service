package cctv.video.service;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import java.util.Collections;
import java.util.Map;

@Controller
public class HomeController {

    @Get("hello")
    public Map<String, Object> index() {
        return Collections.singletonMap("message", "Hello World");
    }
}
