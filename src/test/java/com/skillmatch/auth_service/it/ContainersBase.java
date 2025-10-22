package com.skillmatch.auth_service.it;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class ContainersBase {

    // use a reusable container if you want faster local runs
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("sm_test")
            .withUsername("sm_user")
            .withPassword("sm_pass");

    @BeforeAll
    static void start() { POSTGRES.start(); }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        reg.add("spring.datasource.username", POSTGRES::getUsername);
        reg.add("spring.datasource.password", POSTGRES::getPassword);
        reg.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        reg.add("spring.profiles.active", () -> "test");
    }
}
