/*
 * Copyright 2017 the original author or authors.
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

package org.bremersee.exception;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.bremersee.exception.model.RestApiException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

/**
 * @author Christian Bremer
 */
@Validated
public interface RestApiExceptionMapper {

  String MESSAGE_HEADER_NAME = "X-ERROR-MESSAGE";

  String CODE_HEADER_NAME = "X-ERROR-CODE";

  String CLASS_HEADER_NAME = "X-ERROR-CLASS-NAME";

  @NotNull List<String> getApiPaths();

  @NotNull RestApiException build(
      @NotNull Throwable exception,
      @Nullable String requestPath,
      @Nullable Object handler);

  @NotNull
  HttpStatus detectHttpStatus(@NotNull Throwable exception, @Nullable Object handler);
}