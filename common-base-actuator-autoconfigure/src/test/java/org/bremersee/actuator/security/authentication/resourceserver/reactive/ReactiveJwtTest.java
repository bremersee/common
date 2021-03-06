/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.actuator.security.authentication.resourceserver.reactive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bremersee.actuator.security.authentication.resourceserver.reactive.app.TestConfiguration;
import org.bremersee.test.security.authentication.WithJwtAuthenticationToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;

/**
 * The reactive jwt test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(
    classes = TestConfiguration.class,
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.web-application-type=reactive",
        "spring.application.name=resourceserver-jwt",
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost/jwk/auth",
        "management.endpoints.web.exposure.include=*",
        "bremersee.actuator.auth.enable=auto",
        "bremersee.actuator.auth.jwk-set-uri=http://localhost/jwk/auth",
        "bremersee.actuator.auth.password-flow.token-endpoint=http://localhost/jwk/token",
        "bremersee.actuator.auth.password-flow.client-id=actuator-client",
        "bremersee.actuator.auth.password-flow.client-secret=actuator-client-secret",
        "bremersee.auth.resource-server=auto",
        "bremersee.auth.any-access-mode=deny_all",
        "bremersee.auth.path-matchers[0].ant-pattern=/public/**",
        "bremersee.auth.path-matchers[0].access-mode=permit_all",
        "bremersee.auth.path-matchers[1].ant-pattern=/protected/**",
        "bremersee.auth.path-matchers[1].http-method=POST",
        "bremersee.auth.path-matchers[1].roles=ROLE_ADMIN",
        "bremersee.auth.path-matchers[2].ant-pattern=/protected/**",
        "bremersee.auth.path-matchers[2].roles=ROLE_USER",
        "bremersee.exception-mapping.api-paths=/**"
    })
@TestInstance(Lifecycle.PER_CLASS) // allows us to use @BeforeAll with a non-static method
public class ReactiveJwtTest {

  /**
   * The application context.
   */
  @Autowired
  ApplicationContext context;

  /**
   * The test web client (security configuration is by-passed).
   */
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  WebTestClient webClient;

  /**
   * Setup tests.
   */
  @BeforeAll
  void setUp() {
    // https://docs.spring.io/spring-security/site/docs/current/reference/html/test-webflux.html
    WebTestClient
        .bindToApplicationContext(this.context)
        .configureClient()
        .build();
  }

  /**
   * Gets public.
   */
  @Test
  void getPublic() {
    webClient
        .get()
        .uri("/public")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(response -> assertEquals("public", response));
  }

  /**
   * Gets protected.
   */
  @Test
  @WithJwtAuthenticationToken(roles = "ROLE_USER")
  void getProtected() {
    webClient
        .get()
        .uri("/protected")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(response -> assertEquals("protected", response));
  }

  /**
   * Gets protected and expect forbidden.
   */
  @Test
  @WithJwtAuthenticationToken(roles = "ROLE_SOMETHING")
  void getProtectedAndExpectForbidden() {
    webClient
        .get()
        .uri("/protected")
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  /**
   * Post protected.
   */
  @Test
  @WithJwtAuthenticationToken(roles = "ROLE_ADMIN")
  void postProtected() {
    webClient
        .post()
        .uri("/protected")
        .contentType(MediaType.TEXT_PLAIN)
        .accept(MediaType.TEXT_PLAIN)
        .body(BodyInserters.fromValue("hello"))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(response -> assertEquals("hello", response));
  }

  /**
   * Gets health.
   */
  @Test
  void getHealth() {
    webClient
        .get()
        .uri("/actuator/health")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(response -> assertTrue(response.contains("\"UP\"")));
  }

  /**
   * Gets metrics.
   */
  @Test
  @WithJwtAuthenticationToken(roles = "ROLE_ACTUATOR")
  void getMetrics() {
    webClient
        .get()
        .uri("/actuator/metrics")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(response -> assertTrue(StringUtils.hasText(response)));
  }

  /**
   * Gets metrics and expect forbidden.
   */
  @Test
  @WithJwtAuthenticationToken(roles = "ROLE_USER")
  void getMetricsAndExpectForbidden() {
    webClient
        .get()
        .uri("/actuator/metrics")
        .exchange()
        .expectStatus()
        .isForbidden();
  }

}
