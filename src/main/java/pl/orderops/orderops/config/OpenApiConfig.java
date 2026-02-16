package pl.orderops.orderops.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderopsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OrderOps API")
                        .description("Webhook processing system with rule engine")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("OrderOps Team")
                                .email("team@orderops.local")))
                .addSecurityItem(new SecurityRequirement().addList("API Key"))
                .schemaRequirement("API Key", new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-API-Key")
                        .description("API key for tenant authentication"));
    }
}
