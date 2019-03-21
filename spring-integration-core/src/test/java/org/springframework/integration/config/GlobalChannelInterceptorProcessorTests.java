/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.integration.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.integration.channel.ChannelInterceptorAware;
import org.springframework.integration.channel.interceptor.GlobalChannelInterceptorWrapper;
import org.springframework.messaging.support.ChannelInterceptor;

/**
 * @author Meherzad Lahewala
 *
 * @since 5.0
 */
public class GlobalChannelInterceptorProcessorTests {

	private GlobalChannelInterceptorProcessor globalChannelInterceptorProcessor;

	private ListableBeanFactory beanFactory;

	@Before
	public void setup() {
		this.globalChannelInterceptorProcessor = new GlobalChannelInterceptorProcessor();
		this.beanFactory = mock(ListableBeanFactory.class);
		this.globalChannelInterceptorProcessor.setBeanFactory(this.beanFactory);
	}

	@Test
	public void testProcessorWithNoInterceptor() {
		when(this.beanFactory.getBeansOfType(GlobalChannelInterceptorWrapper.class))
				.thenReturn(Collections.emptyMap());
		this.globalChannelInterceptorProcessor.afterSingletonsInstantiated();
		verify(this.beanFactory)
				.getBeansOfType(GlobalChannelInterceptorWrapper.class);
		verify(this.beanFactory, Mockito.never())
				.getBeansOfType(ChannelInterceptorAware.class);
	}

	@Test
	public void testProcessorWithInterceptorDefaultPattern() {
		Map<String, GlobalChannelInterceptorWrapper> interceptors = new HashMap<>();
		Map<String, ChannelInterceptorAware> channels = new HashMap<>();
		ChannelInterceptor channelInterceptor = Mockito.mock(ChannelInterceptor.class);
		GlobalChannelInterceptorWrapper globalChannelInterceptorWrapper =
				new GlobalChannelInterceptorWrapper(channelInterceptor);

		ChannelInterceptorAware channel = Mockito.mock(ChannelInterceptorAware.class);

		interceptors.put("Test-1", globalChannelInterceptorWrapper);
		channels.put("Test-1", channel);
		when(this.beanFactory.getBeansOfType(GlobalChannelInterceptorWrapper.class))
				.thenReturn(interceptors);
		when(this.beanFactory.getBeansOfType(ChannelInterceptorAware.class))
				.thenReturn(channels);

		this.globalChannelInterceptorProcessor.afterSingletonsInstantiated();

		verify(channel)
				.addInterceptor(channelInterceptor);
	}

	@Test
	public void testProcessorWithInterceptorMatchingPattern() {
		Map<String, GlobalChannelInterceptorWrapper> interceptors = new HashMap<>();
		Map<String, ChannelInterceptorAware> channels = new HashMap<>();
		ChannelInterceptor channelInterceptor = Mockito.mock(ChannelInterceptor.class);
		GlobalChannelInterceptorWrapper globalChannelInterceptorWrapper =
				new GlobalChannelInterceptorWrapper(channelInterceptor);

		ChannelInterceptorAware channel = Mockito.mock(ChannelInterceptorAware.class);

		globalChannelInterceptorWrapper.setPatterns(new String[] { "Te*" });
		interceptors.put("Test-1", globalChannelInterceptorWrapper);
		channels.put("Test-1", channel);
		when(this.beanFactory.getBeansOfType(GlobalChannelInterceptorWrapper.class))
				.thenReturn(interceptors);
		when(this.beanFactory.getBeansOfType(ChannelInterceptorAware.class))
				.thenReturn(channels);
		this.globalChannelInterceptorProcessor.afterSingletonsInstantiated();

		verify(channel)
				.addInterceptor(channelInterceptor);
	}

	@Test
	public void testProcessorWithInterceptorNotMatchingPattern() {
		Map<String, GlobalChannelInterceptorWrapper> interceptors = new HashMap<>();
		Map<String, ChannelInterceptorAware> channels = new HashMap<>();
		ChannelInterceptor channelInterceptor = Mockito.mock(ChannelInterceptor.class);
		GlobalChannelInterceptorWrapper globalChannelInterceptorWrapper =
				new GlobalChannelInterceptorWrapper(channelInterceptor);

		ChannelInterceptorAware channel = Mockito.mock(ChannelInterceptorAware.class);

		globalChannelInterceptorWrapper.setPatterns(new String[] { "te*" });
		interceptors.put("Test-1", globalChannelInterceptorWrapper);
		channels.put("Test-1", channel);
		when(this.beanFactory.getBeansOfType(GlobalChannelInterceptorWrapper.class))
				.thenReturn(interceptors);
		when(this.beanFactory.getBeansOfType(ChannelInterceptorAware.class))
				.thenReturn(channels);

		this.globalChannelInterceptorProcessor.afterSingletonsInstantiated();

		verify(channel, Mockito.never())
				.addInterceptor(channelInterceptor);
	}

	@Test
	public void testProcessorWithInterceptorMatchingNegativePattern() {
		Map<String, GlobalChannelInterceptorWrapper> interceptors = new HashMap<>();
		Map<String, ChannelInterceptorAware> channels = new HashMap<>();
		ChannelInterceptor channelInterceptor = Mockito.mock(ChannelInterceptor.class);
		GlobalChannelInterceptorWrapper globalChannelInterceptorWrapper =
				new GlobalChannelInterceptorWrapper(channelInterceptor);

		ChannelInterceptorAware channel = Mockito.mock(ChannelInterceptorAware.class);

		globalChannelInterceptorWrapper.setPatterns(new String[] { "!te*", "!Te*" });
		interceptors.put("Test-1", globalChannelInterceptorWrapper);
		channels.put("Test-1", channel);
		when(this.beanFactory.getBeansOfType(GlobalChannelInterceptorWrapper.class))
				.thenReturn(interceptors);
		when(this.beanFactory.getBeansOfType(ChannelInterceptorAware.class))
				.thenReturn(channels);
		this.globalChannelInterceptorProcessor.afterSingletonsInstantiated();

		verify(channel, Mockito.never())
				.addInterceptor(channelInterceptor);
	}

}
