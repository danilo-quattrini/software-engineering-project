package it.unicam.cs.ids2425;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication(scanBasePackages = "it.unicam.cs.ids2425")
@EnableJpaRepositories(basePackages = "it.unicam.cs.ids2425")
@EntityScan(basePackages = "it.unicam.cs.ids2425")
@ComponentScan(basePackages = "it.unicam.cs.ids2425")
public class App {

    @RequestMapping("/")
    String home() {
        return "test-repo-project";
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
