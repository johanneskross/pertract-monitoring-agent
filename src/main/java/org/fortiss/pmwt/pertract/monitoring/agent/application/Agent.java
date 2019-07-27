/*******************************************************************************
 * Copyright (c) 2011, 2018 fortiss GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Johannes Kross (fortiss GmbH) - initial implementation
 *
 *******************************************************************************/
package org.fortiss.pmwt.pertract.monitoring.agent.application;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.fortiss.pmwt.pertract.monitoring.agent.configuration.AgentConfig;
import org.fortiss.pmwt.pertract.monitoring.agent.domain.model.CPUSampleRepository;
import org.fortiss.pmwt.pertract.monitoring.agent.domain.service.StacktraceCPUSampler;
import org.fortiss.pmwt.pertract.monitoring.agent.domain.service.ThreadInfoCPUSampler;
import org.fortiss.pmwt.pertract.monitoring.agent.infrastructure.cassandra.StacktraceCassandraCPUSamplingRepository;
import org.fortiss.pmwt.pertract.monitoring.agent.infrastructure.cassandra.ThreadInfoCassandraCPUSamplingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of the agent that contains the premain method
 * 
 * This software is inspired by https://github.com/etsy/statsd-jvm-profiler
 * 
 */
public final class Agent {

	/**
	 * premain method
	 * 
	 * @param 	args 	name for the experiment run 
	 */
	public static void premain(final String concatenatedAgentArgs, final Instrumentation instrumentation) {
		Logger log = LoggerFactory.getLogger(Agent.class);
		AgentConfig agentConfig = new AgentConfig();
		agentConfig.parseArgs(concatenatedAgentArgs);
		log.info("Agent will store samples with experiment name: " + agentConfig.getExperimentName());
		log.info("The following parameters will be stored as part of the samples: " + (agentConfig.isStoreStacktrace() ? ", stacktrace" : "") + (agentConfig.isStoreThreadInfo() ? ", thread info" : ""));
		
		ScheduledExecutorService executors = Executors.newSingleThreadScheduledExecutor();
		if (agentConfig.isStoreStacktrace()) {
			CPUSampleRepository cpuSamplingRepository = new StacktraceCassandraCPUSamplingRepository(agentConfig);
			executors.scheduleAtFixedRate(new StacktraceCPUSampler(cpuSamplingRepository), 5, 100, TimeUnit.MILLISECONDS);
		}
		if (agentConfig.isStoreThreadInfo()) {
			CPUSampleRepository cpuSamplingRepository = new ThreadInfoCassandraCPUSamplingRepository(agentConfig);
			executors.scheduleAtFixedRate(new ThreadInfoCPUSampler(cpuSamplingRepository), 5, 100, TimeUnit.MILLISECONDS);
		}
	}
	
}
