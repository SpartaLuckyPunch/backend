package com.example.burnchuck.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "번쩍 API",
                description = "7조 LUCKY PUNCH 팀의 프로젝트,\n" + "번개 모임 매칭 플랫폼 \"번쩍\" API 문서입니다.",
                version = "v2"
        )
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .tags(List.of(
                        new Tag().name("Auth").description("인증/인가를 다루는 API입니다."),
                        new Tag().name("Category").description("모임을 분류할 수 있는 카테고리를 다루는 API입니다."),
                        new Tag().name("Meeting").description("모임을 다루는 API입니다."),
                        new Tag().name("Like(Meeting)").description("모임에 대해 좋아요를 남기는 기능을 다루는 API입니다."),
                        new Tag().name("Attendance").description("모임에 참여하는 기능을 다루는 API입니다."),
                        new Tag().name("User").description("사용자를 다루는 API입니다."),
                        new Tag().name("Review").description("모임을 함께한 사용자에 대한 리뷰를 다루는 API입니다."),
                        new Tag().name("Follow").description("다른 사용자를 팔로우하는 기능을 다루는 API입니다."),
                        new Tag().name("Notification").description("사용자에게 도착한 알림을 관리하는 API입니다."),
                        new Tag().name("Reaction").description("객관식 형태로 리뷰를 남기는 'Reaction'을 다루는 API입니다.")
                ))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}