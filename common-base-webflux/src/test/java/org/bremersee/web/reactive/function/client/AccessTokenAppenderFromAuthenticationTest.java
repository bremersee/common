/*
 * Copyright 2019 the original author or authors.
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

package org.bremersee.web.reactive.function.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import org.bremersee.test.security.authentication.WithJwtAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * The jwt authentication token appender test.
 *
 * @author Christian Bremer
 */
@SpringJUnitConfig
class AccessTokenAppenderFromAuthenticationTest {

  private static AccessTokenAppender appender = AccessTokenAppender.fromAuthentication();

  private static ClientRequest request = ClientRequest
      .create(HttpMethod.GET, URI.create("http://localhost/resource"))
      .header("Accept", "*/*")
      .build();

  /**
   * Tests filter.
   */
  @Test
  @WithJwtAuthenticationToken
  void filter() {

    ExchangeFunction exchangeFunction = createExchangeFunction();
    StepVerifier.create(appender.filter(request, exchangeFunction))
        .assertNext(response -> {
          assertEquals(HttpStatus.OK, response.statusCode());
          StepVerifier.create(response.bodyToMono(String.class))
              .assertNext(body -> assertEquals("It works", body))
              .expectNextCount(0)
              .verifyComplete();
          verify(exchangeFunction, times(1)).exchange(any(ClientRequest.class));
        })
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Tests filter failure.
   */
  @Test
  void filterFails() {

    ExchangeFunction exchangeFunction = createExchangeFunction();
    StepVerifier.create(appender.filter(request, exchangeFunction))
        .assertNext(response -> {
          assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
          verify(exchangeFunction, times(1)).exchange(any(ClientRequest.class));
        })
        .expectNextCount(0)
        .verifyComplete();
  }

  private static ExchangeFunction createExchangeFunction() {
    ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
    when(exchangeFunction.exchange(any(ClientRequest.class)))
        .then((Answer<Mono<ClientResponse>>) invocationOnMock -> {
          ClientRequest req = invocationOnMock.getArgument(0);
          ClientResponse res;
          if (req.headers().containsKey(HttpHeaders.AUTHORIZATION)) {
            res = ClientResponse.create(HttpStatus.OK)
                .body("It works")
                .build();
          } else {
            res = ClientResponse.create(HttpStatus.UNAUTHORIZED).build();
          }
          return Mono.just(res);
        });
    return exchangeFunction;
  }

}