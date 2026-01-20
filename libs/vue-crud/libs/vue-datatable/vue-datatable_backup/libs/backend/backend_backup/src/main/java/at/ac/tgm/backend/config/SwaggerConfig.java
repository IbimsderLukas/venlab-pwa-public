package at.ac.tgm.backend.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Venlab REST API")
                        .version("1.0.0")
                        .description("CRUD API für Analysis, Sample, Box, BoxPos, Log, Threshold"));
    }
}
