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
package org.fortiss.pmwt.pertract.monitoring.agent.domain.model;

import java.util.Date;
import java.util.Map;

/**
 * Repository of domain model for samples of CPU traces
 */
public interface CPUSampleRepository {

	/**
	 * Store method for the data model
	 * 
	 * @param 	sampleTime 	the time at which the sample was measured
	 * @param 	samples 	the cpu trace consisting of the thread and the stack trace elements
	 */
	void storeSamples(Date sampleTime, Map<String, Long> samples);
	
}
