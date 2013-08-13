/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.javawithmarcus.wicket.cdi;

import java.util.Map;
import javax.enterprise.context.Conversation;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import com.github.javawithmarcus.wicket.cdi.BehaviorInjector;
import com.github.javawithmarcus.wicket.cdi.CdiConfiguration;
import com.github.javawithmarcus.wicket.cdi.CdiShutdownCleaner;
import com.github.javawithmarcus.wicket.cdi.CdiWebApplicationFactory;
import com.github.javawithmarcus.wicket.cdi.ComponentInjector;
import com.github.javawithmarcus.wicket.cdi.ConversationExpiryChecker;
import com.github.javawithmarcus.wicket.cdi.ConversationManager;
import com.github.javawithmarcus.wicket.cdi.ConversationPropagator;
import com.github.javawithmarcus.wicket.cdi.DetachEventEmitter;
import com.github.javawithmarcus.wicket.cdi.NonContextualManager;
import com.github.javawithmarcus.wicket.cdi.SessionInjector;
import com.github.javawithmarcus.wicket.cdi.testapp.TestAppScope;
import com.github.javawithmarcus.wicket.cdi.testapp.TestCdiApplication;
import com.github.javawithmarcus.wicket.cdi.testapp.TestConversationBean;
import com.github.javawithmarcus.wicket.cdi.util.tester.CdiWicketTester;
import com.github.javawithmarcus.wicket.cdi.util.tester.FilterConfigProducer;
import com.github.javawithmarcus.wicket.cdi.util.tester.TestBehaviorInjector;
import com.github.javawithmarcus.wicket.cdi.util.tester.TestCdiConfiguration;
import com.github.javawithmarcus.wicket.cdi.util.tester.TestComponentInjector;

/**
 * @author jsarman
 */
@RunWith(CdiRunner.class)
@ActivatedAlternatives({TestBehaviorInjector.class, TestComponentInjector.class, TestCdiConfiguration.class})
@AdditionalClasses({
		CdiWicketTester.class,
		BehaviorInjector.class,
		CdiConfiguration.class,
		CdiShutdownCleaner.class,
		ComponentInjector.class,
		ConversationExpiryChecker.class,
		ConversationPropagator.class,
		ConversationManager.class,
		DetachEventEmitter.class,
		NonContextualManager.class,
		SessionInjector.class,
		MockCdiContainer.class,
		TestAppScope.class,
		TestConversationBean.class,
		FilterConfigProducer.class,
		TestCdiApplication.class,
		CdiWebApplicationFactory.class})
public abstract class WicketCdiTestCase extends Assert
{
	@Inject
	private Instance<CdiWicketTester> testers;

	private CdiWicketTester instantiatedTester;

	@Inject
	Conversation conversation;

	@Inject
	FilterConfigProducer filterConfigProducer;

	public CdiWicketTester getTester()
	{
		if (instantiatedTester == null)
		{
			instantiatedTester = testers.get();
		}
		return instantiatedTester;
	}

	public CdiWicketTester getTester(boolean newTest)
	{
		if (newTest)
		{
			return testers.get();
		}
		return getTester();
	}

	public CdiWicketTester getTester(Map<String, String> customParamters)
	{
		if (instantiatedTester != null)
		{
			throw new IllegalStateException("The Wicket Tester is already initialized.");
		}
		filterConfigProducer.addParameters(customParamters);
		return getTester();
	}

	public CdiWicketTester getTester(boolean newTest, Map<String, String> customParamters)
	{
		if (newTest)
		{
			filterConfigProducer.addParameters(customParamters);
			return testers.get();
		}
		return getTester(customParamters);
	}

	@Before
	public void init()
	{
		getTester();
	}

	@After
	public void end()
	{
		if (instantiatedTester != null)
		{
			if (!conversation.isTransient())
			{
				conversation.end();
			}
		}
	}

}
