/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.support.management.graph;

import java.util.Collection;

import org.springframework.messaging.MessageHandler;

/**
 * Represents an endpoint that can route to multiple channels and can emit errors
 * (pollable endpoint).
 *
 * @author Gary Russell
 * @since 4.3
 *
 */
public class ErrorCapableRoutingNode extends RoutingMessageHandlerNode implements ErrorCapableNode {

	private final String errors;

	public ErrorCapableRoutingNode(int nodeId, String name, MessageHandler handler, String input, String output,
			String errors, Collection<String> routes) {
		super(nodeId, name, handler, input, output, routes);
		this.errors = errors;
	}

	@Override
	public String getErrors() {
		return this.errors;
	}

}
