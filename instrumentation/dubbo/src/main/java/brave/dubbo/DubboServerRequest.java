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
import brave.rpc.RpcServerRequest;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

final class DubboServerRequest extends RpcServerRequest implements DubboRequest {
  final Invoker<?> invoker;
  final Invocation invocation;

  DubboServerRequest(Invoker<?> invoker, Invocation invocation) {
    if (invoker == null)
      throw new NullPointerException("invoker == null");
    if (invocation == null)
      throw new NullPointerException("invocation == null");
    this.invoker = invoker;
    this.invocation = invocation;
  }

  @Override
  public Invoker<?> invoker() {
    return invoker;
  }

  @Override
  public Invocation invocation() {
    return invocation;
  }

  /**
   * Returns the {@link Invocation}.
   */
  @Override
  public Invocation unwrap() {
    return invocation;
  }

  /**
   * Returns the method name of the invocation or the first string arg of an "$invoke" or
   * "$invokeAsync" method.
   */
  @Override
  public String method() {
    return DubboParser.method(invocation);
  }

  /**
   * Returns the {@link URL#getServiceInterface() service interface} of the invocation.
   */
  @Override
  public String service() {
    return DubboParser.service(invoker);
  }

  @Override
  public boolean parseRemoteIpAndPort(Span span) {
    return DubboParser.parseRemoteIpAndPort(span);
  }

  @Override
  protected String propagationField(String keyName) {
    return invocation.getAttachment(keyName);
  }
}
