/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.integration.channel.factory;

import java.util.List;

import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.DispatcherPolicy;
import org.springframework.integration.message.Message;
import org.springframework.integration.message.selector.MessageSelector;

/**
 * @author Marius Bogoevici
 */
public class StubChannel extends AbstractMessageChannel {

	public StubChannel(DispatcherPolicy dispatcherPolicy) {
		super(dispatcherPolicy);
	}

	@Override
	protected Message<?> doReceive(long timeout) {
		return null;
	}

	@Override
	protected boolean doSend(Message<?> message, long timeout) {
		return false;
	}

	public List<Message<?>> clear() {
		return null;
	}

	public List<Message<?>> purge(MessageSelector selector) {
		return null;
	}
	
}
