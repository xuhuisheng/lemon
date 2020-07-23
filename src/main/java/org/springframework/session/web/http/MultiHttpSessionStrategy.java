/*
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.session.web.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>
 * Some {@link HttpSessionStrategy} may also want to further customize
 * {@link HttpServletRequest} and {@link HttpServletResponse} objects. For
 * example, {@link CookieHttpSessionStrategy} customizes how URL rewriting is
 * done to select which session should be used in the event multiple sessions
 * are active.
 * </p>
 *
 * @see CookieHttpSessionStrategy
 *
 * @author Rob Winch
 * @since 1.0
 */
public interface MultiHttpSessionStrategy extends HttpSessionStrategy, RequestResponsePostProcessor {
}