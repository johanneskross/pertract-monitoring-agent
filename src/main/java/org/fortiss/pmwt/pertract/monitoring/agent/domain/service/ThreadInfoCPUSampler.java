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
package org.fortiss.pmwt.pertract.monitoring.agent.domain.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.fortiss.pmwt.pertract.monitoring.agent.application.Agent;
import org.fortiss.pmwt.pertract.monitoring.agent.domain.model.CPUSampleRepository;

/**
 * Domain service to sample CPU traces, including stacktraces of the threads
 */
public class ThreadInfoCPUSampler extends CPUSampler {

	public ThreadInfoCPUSampler(CPUSampleRepository cpuSamplingRepository) {
		super(cpuSamplingRepository);
	}

	/**
	 * Fetches and stores a batch of currently active JVM threads and will be invoked periodically by {@link Agent}.
	 * 
	 * Note: The cpu sample values will increase steadily over time across intervals
	 */
	public void run() {
		Date sampleTime = new Date();
		Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();

		Map<String, Long> samplesFlinkTable = new HashMap<String, Long>();
		for (Entry<Thread, StackTraceElement[]> stackTrace : stackTraces.entrySet()) {
			String threadNameAndGroup = getThreadNameAndGroup(stackTrace.getKey());
			long threadCpuTime = threadMXBean.getThreadCpuTime(stackTrace.getKey().getId()) / nano_to_milli_divisor;
			addSamples(samplesFlinkTable, threadNameAndGroup, threadCpuTime);
		}
		
		cpuSamplingRepository.storeSamples(sampleTime, samplesFlinkTable);
	}


	/**
	 * Formats the thread name and thread group
	 * @param thread The thread from which the thread name and thread group should be extracted and formatted
	 */
	private String getThreadNameAndGroup(Thread thread) {
		return thread.getThreadGroup().getName() + "--" + thread.getName();
	}


}
