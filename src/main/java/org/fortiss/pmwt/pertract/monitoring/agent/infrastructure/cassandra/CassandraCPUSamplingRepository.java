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
package org.fortiss.pmwt.pertract.monitoring.agent.infrastructure.cassandra;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.fortiss.pmwt.pertract.monitoring.agent.configuration.AgentConfig;
import org.fortiss.pmwt.pertract.monitoring.agent.configuration.CassandraConnectionDetails;
import org.fortiss.pmwt.pertract.monitoring.agent.domain.model.CPUSampleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

/**
 * Implementation of {@link CPUSampleRepository} for storing samples in Apache Cassandra
 */
public abstract class CassandraCPUSamplingRepository implements CPUSampleRepository {
	
	protected Cluster cluster;
	protected Session session;
	protected String experimentName;
	protected String jvm;
	protected PreparedStatement preparedStatement;
	private static final Logger log = LoggerFactory.getLogger(CassandraCPUSamplingRepository.class);
	
	/** 
	 * Public constructor
	 * 
	 * @param 	agentConfig 	 
	*/
	public CassandraCPUSamplingRepository(AgentConfig agentConfig) {
		this.experimentName = agentConfig.getExperimentName();
		jvm = ManagementFactory.getRuntimeMXBean().getName();
		this.cluster = Cluster.builder().addContactPoint(CassandraConnectionDetails.host).build();
		this.session = this.cluster.connect();
		this.createKeyspaceIfNotExists();
		this.session.close();
		this.session = this.cluster.connect(CassandraConnectionDetails.keyspace);
		this.createTableIfNotExists();
		this.preparedStatement = prepareStatement();
		log.info("Connected to Cassandra repository");
	}
	
	/** Creates a new keyspace / database if it does not exist already */
	private void createKeyspaceIfNotExists() {
		String statement = "CREATE KEYSPACE IF NOT EXISTS " + CassandraConnectionDetails.keyspace 
				+ " WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};";
		this.session.execute(statement);
		log.info("Created keyspace: \n" + statement);
	}
	
	/** Creates a new table if it does not exist already */
	public abstract void createTableIfNotExists();
	
	/** Prepares the statement */
	public abstract PreparedStatement prepareStatement();
	
	/** {@inheritDoc} */
	@Override
	public void storeSamples(Date sampleTime,  Map<String, Long> samples) {
		BatchStatement batchStatement = new BatchStatement();
		for (Entry<String, Long> sample : samples.entrySet()) {
			BoundStatement boundStatement = new BoundStatement(this.preparedStatement);
			batchStatement.add(boundStatement.bind(
					experimentName,
					jvm,
					sample.getKey(),
					sampleTime, 
					sample.getValue()
				)
			);
		}
		
		session.execute(batchStatement);
	}

}
