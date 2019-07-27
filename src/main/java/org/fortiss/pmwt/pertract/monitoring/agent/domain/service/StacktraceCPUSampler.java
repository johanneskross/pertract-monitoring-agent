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
public class StacktraceCPUSampler extends CPUSampler {

	public StacktraceCPUSampler(CPUSampleRepository cpuSamplingRepository) {
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

		Map<String, Long> samples = new HashMap<String, Long>();
		for (Entry<Thread, StackTraceElement[]> stackTrace : stackTraces.entrySet()) {
			String joinedStackTrace = getJoinedStackTrace(stackTrace.getValue());
			long threadCpuTime = threadMXBean.getThreadCpuTime(stackTrace.getKey().getId()) / nano_to_milli_divisor;
			addSamples(samples, joinedStackTrace, threadCpuTime);
		}

		cpuSamplingRepository.storeSamples(sampleTime, samples);
	}


	/**
	 * Formats the stacktraces
	 * 
	 *  @param stackTraceElements An array of stacktrace elements to be formatted
	 */
	private String getJoinedStackTrace(StackTraceElement[] stackTraceElements) {
		String joinedStackTrace = "cpu";
		for (int i = stackTraceElements.length-1; i >=0 ; i--) {
			joinedStackTrace += "--";
			joinedStackTrace += stackTraceElements[i].getClassName() + ".";
			joinedStackTrace += stackTraceElements[i].getMethodName() + ":";
			joinedStackTrace += stackTraceElements[i].getLineNumber();
		}
		return joinedStackTrace;
	}

}
