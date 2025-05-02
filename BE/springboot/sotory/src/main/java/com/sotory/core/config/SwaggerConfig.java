package com.sotory.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        servers = {
        @Server(url = "/api", description = "local server"),
        @Server(url = "https://j12a503.p.ssafy.io/api", description = "deploy server")
    }
)
public class SwaggerConfig {

    @Bean
    OpenAPI openAPI() {
        Info info = new Info()
                .title("API 명세서")
                .description("<h3>API Reference for Developers</h3> API 명세서")
                .version("v3")
                .contact(new io.swagger.v3.oas.models.info.Contact()
                        .name("yujeong")
                        .email("hao_yj@naver.com")
                        .url("http://edu.ssafy.com"));

        return new OpenAPI()
                .info(info)
//                .components(securityComponents())
                .addSecurityItem(new SecurityRequirement().addList("Bearer jwt"));
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all") // 그룹 이름 지정

                .pathsToMatch( "/**")
                .build();
    }

    @Bean
    GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("users")
                .pathsToMatch("/users/**")
                .build();
    }

    @Bean
    GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/auth/**")
                .build();
    }


//    private Components securityComponents() {
//        return new Components()
//                .addSecuritySchemes("Bearer jwt", new SecurityScheme()
//                        .type(SecurityScheme.Type.HTTP)
//                        .scheme("Bearer")
//                        .bearerFormat("JWT")
//                        .in(SecurityScheme.In.HEADER)
//                        .name("Authorization"));
//    }

    // 로그인, 로그아웃
    private Paths customPaths() {
        Paths paths = new Paths();

        paths.addPathItem("/auth/kakao", new PathItem()
                .post(new io.swagger.v3.oas.models.Operation()
                        .summary("카카오 로그인")
                        .addTagsItem("auth")
                        .responses(new ApiResponses()
                                .addApiResponse("302", new ApiResponse().description("카카오 인증 페이지로 리다이렉트"))
                        )
                ));
//        paths.addPathItem("/users/logout", new PathItem()
//                .post(new io.swagger.v3.oas.models.Operation()
//                        .summary("로그아웃")
//                        .addTagsItem("auth")
//                        .responses(new ApiResponses()
//                                .addApiResponse("200", new ApiResponse().description("로그아웃 성공").content(new Content().addMediaType("application/json", new MediaType().schema(new Schema<String>().type("string")))))
//                                .addApiResponse("401", new ApiResponse().description("인증 실패"))
//                        )
//                ));

        return paths;
    }
}

