package tinyledger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI tinyLedgerOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Tiny Ledger API")
                .description("A simple multi-account ledger application that provides basic banking operations")
                .version("1.0"));
    }
} 