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

package org.bremersee.actuator.security.authentication.resourceserver.servlet;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bremersee.actuator.security.authentication.resourceserver.servlet.app.TestConfiguration;
import org.bremersee.test.security.authentication.WithJwtAuthenticationToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * The jwt test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(
    classes = TestConfiguration.class,
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.web-application-type=servlet",
        "spring.application.name=resourceserver-jwt",
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost/jwk",
        "management.endpoints.web.exposure.include=*",
        "bremersee.actuator.auth.enable=auto",
        "bremersee.actuator.auth.jwk-set-uri=http://localhost/jwk",
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
public class JwtTest {

  /**
   * The context.
   */
  @Autowired
  WebApplicationContext context;

  /**
   * The mock mvc.
   */
  MockMvc mvc;

  /**
   * Setup mock mvc.
   */
  @BeforeAll
  void setup() {
    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  /**
   * Gets public.
   *
   * @throws Exception the exception
   */
  @Test
  void getPublic() throws Exception {
    mvc.perform(get("/public")
        .accept(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(content().string("public"));
  }

  /**
   * Gets protected.
   *
   * @throws Exception the exception
   */
  @Test
  @WithJwtAuthenticationToken(roles = "ROLE_USER")
  void getProtected() throws Exception {
    mvc.perform(get("/protected"))
        .andExpect(status().isOk())
        .andExpect(content().string("protected"));
  }

  /**
   * Gets protected and expect forbidden.
   *
   * @throws Exception the exception
   */
  @Test
  @WithJwtAuthenticationToken(roles = "ROLE_SOMETHING")
  void getProtectedAndExpectForbidden() throws Exception {
    mvc.perform(get("/protected"))
        .andExpect(status().isForbidden());
  }

  /**
   * Post protected.
   *
   * @throws Exception the exception
   */
  @Test
  @WithJwtAuthenticationToken(roles = "ROLE_ADMIN")
  void postProtected() throws Exception {
    mvc.perform(post("/protected")
        .accept(MediaType.TEXT_PLAIN)
        .contentType(MediaType.TEXT_PLAIN)
        .content("Good Morning"))
        .andExpect(status().isOk())
        .andExpect(content().string("Good Morning"));
  }

  /**
   * Gets public.
   *
   * @throws Exception the exception
   */
  @Test
  void getHealth() throws Exception {
    mvc.perform(get("/actuator/health"))
        .andExpect(status().isOk());
  }

  /**
   * Gets metrics.
   *
   * @throws Exception the exception
   */
  @Test
  @WithJwtAuthenticationToken(roles = "ROLE_ACTUATOR")
  void getMetrics() throws Exception {
    mvc.perform(get("/actuator/metrics"))
        .andExpect(status().isOk());
  }

  /**
   * Gets metrics and expect forbidden.
   *
   * @throws Exception the exception
   */
  @Test
  @WithJwtAuthenticationToken(roles = "ROLE_USER")
  void getMetricsAndExpectForbidden() throws Exception {
    mvc.perform(get("/actuator/metrics"))
        .andExpect(status().isForbidden());
  }

}
