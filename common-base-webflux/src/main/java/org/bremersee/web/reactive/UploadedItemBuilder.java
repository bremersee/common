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

package org.bremersee.web.reactive;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.bremersee.web.ReqParam;
import org.bremersee.web.UploadedItem;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The reactive uploaded item builder.
 *
 * @author Christian Bremer
 */
public interface UploadedItemBuilder {

  /**
   * Build uploaded item from multi parts.
   *
   * @param contentPart the multi part with the uploaded content, can be a {@code FilePart} or a
   *     {@code FormFieldPart} with a RFC 2397 data uri
   * @return the uploaded item; if the content part is {@code null}, an empty {@link UploadedItem}
   *     will be returned
   */
  Mono<UploadedItem<?>> build(@Nullable Part contentPart);

  /**
   * Build list of uploaded items from the given multi part data.
   *
   * @param multiPartData the multi part data
   * @param requestParameters the request parameters
   * @return the list of uploaded items
   */
  Mono<List<UploadedItem<?>>> buildFromFirstParameterValue(
      @NotNull MultiValueMap<String, Part> multiPartData,
      ReqParam... requestParameters);

  /**
   * Build list (flux) of lists of uploaded items from the given multi part data.
   *
   * @param multiPartData the multi part data
   * @param requestParameters the request parameters
   * @return the list (flux) of lists of uploaded items
   */
  Flux<List<UploadedItem<?>>> buildFromAllParameterValues(
      @NotNull MultiValueMap<String, Part> multiPartData,
      ReqParam... requestParameters);

  /**
   * Build map of uploaded items from the given multi part data.
   *
   * @param multiPartData the multi part data
   * @param requestParameters the request parameters
   * @return the map of uploaded items
   */
  Mono<Map<String, UploadedItem<?>>> buildMapFromFirstParameterValue(
      @NotNull MultiValueMap<String, Part> multiPartData,
      ReqParam... requestParameters);

  /**
   * Build multi value map of uploaded items from the given multi part data.
   *
   * @param multiPartData the multi part data
   * @param requestParameters the request parameters
   * @return the mono
   */
  Mono<MultiValueMap<String, UploadedItem<?>>> buildMapFromAllParameterValues(
      @NotNull MultiValueMap<String, Part> multiPartData,
      ReqParam... requestParameters);

  /**
   * Gets uploaded item with the specified index. If the list is smaller than the index, an empty
   * {@link UploadedItem} will be returned.
   *
   * @param list the list
   * @param index the index
   * @return the uploaded item
   */
  static UploadedItem<?> getUploadedItem(List<UploadedItem<?>> list, int index) {
    return list != null && index >= 0 && list.size() > index ? list.get(index) : UploadedItem.EMPTY;
  }

  /**
   * Gets uploaded item with the specified request parameter name. If no such uploaded item exists,
   * an empty one will be returned.
   *
   * @param map the map
   * @param name the request parameter name
   * @return the uploaded item
   */
  static UploadedItem<?> getUploadedItem(Map<String, UploadedItem<?>> map, String name) {
    return map != null && name != null ? map.getOrDefault(name, UploadedItem.EMPTY)
        : UploadedItem.EMPTY;
  }

  /**
   * Default reactive uploaded item builder.
   *
   * @return the reactive uploaded item builder
   */
  static UploadedItemBuilder defaultBuilder() {
    return new UploadedItemBuilderImpl();
  }

  /**
   * Default reactive uploaded item builder.
   *
   * @param tmpDir the tmp dir
   * @return the reactive uploaded item builder
   */
  static UploadedItemBuilder defaultBuilder(String tmpDir) {
    return new UploadedItemBuilderImpl(tmpDir);
  }

  /**
   * Default reactive uploaded item builder.
   *
   * @param tmpDir the tmp dir
   * @return the reactive uploaded item builder
   */
  static UploadedItemBuilder defaultBuilder(Path tmpDir) {
    return new UploadedItemBuilderImpl(tmpDir);
  }

  /**
   * Default reactive uploaded item builder.
   *
   * @param tmpDir the tmp dir
   * @return the reactive uploaded item builder
   */
  static UploadedItemBuilder defaultBuilder(File tmpDir) {
    return new UploadedItemBuilderImpl(tmpDir);
  }

}