package org.fortiss.pmwt.pertract.monitoring.agent.infrastructure.cassandra;

import org.fortiss.pmwt.pertract.monitoring.agent.configuration.AgentConfig;
import org.fortiss.pmwt.pertract.monitoring.agent.configuration.CassandraConnectionDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.PreparedStatement;

public class ThreadInfoCassandraCPUSamplingRepository extends CassandraCPUSamplingRepository {
	
	private static final Logger log = LoggerFactory.getLogger(ThreadInfoCassandraCPUSamplingRepository.class);
	
	public ThreadInfoCassandraCPUSamplingRepository(AgentConfig agentConfig) {
		super(agentConfig);
	}

	@Override
	public void createTableIfNotExists() {
		String statement = "CREATE TABLE IF NOT EXISTS " + CassandraConnectionDetails.keyspace + "." + CassandraConnectionDetails.tableThreadInfo + " ("
				+ "experiment_name text, "
				+ "jvm text, "
				+ "thread_group_and_name text, "
				+ "sample_time timestamp, "
				+ "cpu_time bigint, "
				+ "PRIMARY KEY ((experiment_name, jvm, thread_group_and_name), sample_time)) "
				+ "WITH CLUSTERING ORDER BY (sample_time DESC)";
		this.session.execute(statement);
		log.info("Created table: \n" + statement);
	}

	@Override
	public PreparedStatement prepareStatement() {
		String statement = "INSERT INTO " + CassandraConnectionDetails.tableThreadInfo + " ("
				+ "experiment_name, jvm, thread_group_and_name, sample_time, cpu_time) "
				+ "VALUES (?,?,?,?,?)";
		log.info("Prepare session for Flink table with statement: \n" + statement);
		return this.session.prepare(statement);
	}

}
