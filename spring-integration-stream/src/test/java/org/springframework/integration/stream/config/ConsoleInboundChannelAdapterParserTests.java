/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.integration.stream.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.support.context.NamedComponent;
import org.springframework.integration.test.util.TestUtils;
import org.springframework.messaging.Message;

/**
 * @author Mark Fisher
 * @author Gunnar Hillert
 * @author Gary Russell
 */
public class ConsoleInboundChannelAdapterParserTests {

	@Before
	public void writeTestInput() {
		ByteArrayInputStream stream = new ByteArrayInputStream("foo".getBytes());
		System.setIn(stream);
	}

	@Test
	public void adapterWithDefaultCharset() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"consoleInboundChannelAdapterParserTests.xml", ConsoleInboundChannelAdapterParserTests.class);
		SourcePollingChannelAdapter adapter = context.getBean("adapterWithDefaultCharset.adapter",
				SourcePollingChannelAdapter.class);
		MessageSource<?> source = (MessageSource<?>) new DirectFieldAccessor(adapter).getPropertyValue("source");
		assertTrue(source instanceof NamedComponent);
		assertEquals("adapterWithDefaultCharset.adapter", adapter.getComponentName());
		assertEquals("stream:stdin-channel-adapter(character)", adapter.getComponentType());
		assertEquals("stream:stdin-channel-adapter(character)", ((NamedComponent) source).getComponentType());
		DirectFieldAccessor sourceAccessor = new DirectFieldAccessor(source);
		Reader bufferedReader = (Reader) sourceAccessor.getPropertyValue("reader");
		assertEquals(BufferedReader.class, bufferedReader.getClass());
		DirectFieldAccessor bufferedReaderAccessor = new DirectFieldAccessor(bufferedReader);
		Reader reader = (Reader) bufferedReaderAccessor.getPropertyValue("in");
		assertEquals(InputStreamReader.class, reader.getClass());
		Charset readerCharset = Charset.forName(((InputStreamReader) reader).getEncoding());
		assertEquals(Charset.defaultCharset(), readerCharset);
		Message<?> message = source.receive();
		assertNotNull(message);
		assertEquals("foo", message.getPayload());
		adapter = context.getBean("pipedAdapterNoCharset.adapter", SourcePollingChannelAdapter.class);
		source = adapter.getMessageSource();
		assertTrue(TestUtils.getPropertyValue(source, "blockToDetectEOF", Boolean.class));
		context.close();
	}

	@Test
	public void adapterWithProvidedCharset() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"consoleInboundChannelAdapterParserTests.xml", ConsoleInboundChannelAdapterParserTests.class);
		SourcePollingChannelAdapter adapter = context.getBean("adapterWithProvidedCharset.adapter",
				SourcePollingChannelAdapter.class);
		MessageSource<?> source = adapter.getMessageSource();
		DirectFieldAccessor sourceAccessor = new DirectFieldAccessor(source);
		Reader bufferedReader = (Reader) sourceAccessor.getPropertyValue("reader");
		assertEquals(BufferedReader.class, bufferedReader.getClass());
		assertEquals(false, sourceAccessor.getPropertyValue("blockToDetectEOF"));
		DirectFieldAccessor bufferedReaderAccessor = new DirectFieldAccessor(bufferedReader);
		Reader reader = (Reader) bufferedReaderAccessor.getPropertyValue("in");
		assertEquals(InputStreamReader.class, reader.getClass());
		Charset readerCharset = Charset.forName(((InputStreamReader) reader).getEncoding());
		assertEquals(Charset.forName("UTF-8"), readerCharset);
		Message<?> message = source.receive();
		assertNotNull(message);
		assertEquals("foo", message.getPayload());
		adapter = context.getBean("pipedAdapterWithCharset.adapter", SourcePollingChannelAdapter.class);
		source = adapter.getMessageSource();
		assertTrue(TestUtils.getPropertyValue(source, "blockToDetectEOF", Boolean.class));
		bufferedReader = (Reader) sourceAccessor.getPropertyValue("reader");
		assertEquals(BufferedReader.class, bufferedReader.getClass());
		bufferedReaderAccessor = new DirectFieldAccessor(bufferedReader);
		reader = (Reader) bufferedReaderAccessor.getPropertyValue("in");
		assertEquals(InputStreamReader.class, reader.getClass());
		readerCharset = Charset.forName(((InputStreamReader) reader).getEncoding());
		assertEquals(Charset.forName("UTF-8"), readerCharset);
		context.close();
	}

	@Test
	public void testConsoleSourceWithInvalidCharset() {
		BeanCreationException beanCreationException = null;
		try {
			new ClassPathXmlApplicationContext("invalidConsoleInboundChannelAdapterParserTests.xml",
					ConsoleInboundChannelAdapterParserTests.class).close();
		}
		catch (BeanCreationException e) {
			beanCreationException = e;
		}
		Throwable rootCause = beanCreationException.getRootCause();
		assertEquals(UnsupportedEncodingException.class, rootCause.getClass());
	}

}
