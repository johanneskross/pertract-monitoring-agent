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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;

import org.fortiss.pmwt.pertract.monitoring.agent.domain.model.CPUSampleRepository;

/**
 * Domain service to sample CPU traces
 */
public abstract class CPUSampler implements Runnable {

	protected static final int nano_to_milli_divisor = 1000000;
	
	protected static ThreadMXBean threadMXBean;
	protected CPUSampleRepository cpuSamplingRepository;
	
	/**
	 * Public constructor
	 * 
	 * @param 	cpuSamplingRepository 	repository to store the samples of CPU traces
	 */
	public CPUSampler(CPUSampleRepository cpuSamplingRepository) {
		threadMXBean = ManagementFactory.getThreadMXBean();
		this.cpuSamplingRepository = cpuSamplingRepository;
	}

	public void addSamples(Map<String, Long> samples, String key, long threadCpuTime) {
		if (samples.containsKey(key)) {
			samples.put(key, samples.get(key) + threadCpuTime);
		} else {
			samples.put(key, threadCpuTime);
		}
	}

}
