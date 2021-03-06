package findingsAnalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collections;

@SpringBootApplication
public class FindingsAnalyzerBac2Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(FindingsAnalyzerBac2Application.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8084"));
        app.run(args);
    }
}
