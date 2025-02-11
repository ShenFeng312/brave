/*
 * Copyright 2013-2023 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package brave.dubbo;

import brave.Span;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static brave.Span.Kind.CLIENT;
import static brave.Span.Kind.SERVER;
import static org.mockito.Mockito.mock;

public class FinishSpanTest extends ITTracingFilter {
  DubboClientRequest clientRequest =
      new DubboClientRequest(mock(Invoker.class), mock(Invocation.class), Collections.emptyMap());
  DubboServerRequest serverRequest =
      new DubboServerRequest(mock(Invoker.class), mock(Invocation.class));
  TracingFilter filter;

  @Before public void setup() {
    filter = init();
  }

  @Test public void finish_null_result_and_error_DubboClientRequest() {
    Span span = tracing.tracer().nextSpan().kind(CLIENT).start();

    FinishSpan.finish(filter, clientRequest, null, null, span);

    testSpanHandler.takeRemoteSpan(CLIENT);
  }

  @Test public void finish_null_result_and_error_DubboServerRequest() {
    Span span = tracing.tracer().nextSpan().kind(SERVER).start();

    FinishSpan.finish(filter, serverRequest, null, null, span);

    testSpanHandler.takeRemoteSpan(SERVER);
  }

  @Test public void finish_result_but_null_error_DubboClientRequest() {
    Span span = tracing.tracer().nextSpan().kind(CLIENT).start();

    FinishSpan.finish(filter, clientRequest, mock(Result.class), null, span);

    testSpanHandler.takeRemoteSpan(CLIENT);
  }

  @Test public void finish_result_but_null_error_DubboServerRequest() {
    Span span = tracing.tracer().nextSpan().kind(SERVER).start();

    FinishSpan.finish(filter, serverRequest, mock(Result.class), null, span);

    testSpanHandler.takeRemoteSpan(SERVER);
  }

  @Test public void finish_error_but_null_result_DubboClientRequest() {
    Span span = tracing.tracer().nextSpan().kind(CLIENT).start();

    Throwable error = new RuntimeException("melted");
    FinishSpan.finish(filter, clientRequest, null, error, span);

    testSpanHandler.takeRemoteSpanWithError(CLIENT, error);
  }

  @Test public void finish_error_but_null_result_DubboServerRequest() {
    Span span = tracing.tracer().nextSpan().kind(SERVER).start();

    Throwable error = new RuntimeException("melted");
    FinishSpan.finish(filter, serverRequest, null, error, span);

    testSpanHandler.takeRemoteSpanWithError(SERVER, error);
  }

  @Test public void create_null_result_value_and_error_DubboClientRequest() {
    Span span = tracing.tracer().nextSpan().kind(CLIENT).start();

    FinishSpan.create(filter, clientRequest, mock(Result.class), span)
        .accept(null, null);

    testSpanHandler.takeRemoteSpan(CLIENT);
  }

  @Test public void create_null_result_value_and_error_DubboServerRequest() {
    Span span = tracing.tracer().nextSpan().kind(SERVER).start();

    FinishSpan.create(filter, serverRequest, mock(Result.class), span)
        .accept(null, null);

    testSpanHandler.takeRemoteSpan(SERVER);
  }

  @Test public void create_result_value_but_null_error_DubboClientRequest() {
    Span span = tracing.tracer().nextSpan().kind(CLIENT).start();

    FinishSpan.create(filter, clientRequest, mock(Result.class), span)
        .accept(new Object(), null);

    testSpanHandler.takeRemoteSpan(CLIENT);
  }

  @Test public void create_result_value_but_null_error_DubboServerRequest() {
    Span span = tracing.tracer().nextSpan().kind(SERVER).start();

    FinishSpan.create(filter, serverRequest, mock(Result.class), span)
        .accept(new Object(), null);

    testSpanHandler.takeRemoteSpan(SERVER);
  }

  @Test public void create_error_but_null_result_value_DubboClientRequest() {
    Span span = tracing.tracer().nextSpan().kind(CLIENT).start();

    Throwable error = new RuntimeException("melted");
    FinishSpan.create(filter, clientRequest, mock(Result.class), span)
        .accept(null, error);

    testSpanHandler.takeRemoteSpanWithError(CLIENT, error);
  }

  @Test public void create_error_but_null_result_value_DubboServerRequest() {
    Span span = tracing.tracer().nextSpan().kind(SERVER).start();

    Throwable error = new RuntimeException("melted");
    FinishSpan.create(filter, serverRequest, mock(Result.class), span)
        .accept(null, error);

    testSpanHandler.takeRemoteSpanWithError(SERVER, error);
  }
}
