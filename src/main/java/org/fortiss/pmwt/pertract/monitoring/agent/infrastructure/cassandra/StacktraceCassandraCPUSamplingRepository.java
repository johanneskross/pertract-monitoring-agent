package org.fortiss.pmwt.pertract.monitoring.agent.infrastructure.cassandra;

import org.fortiss.pmwt.pertract.monitoring.agent.configuration.AgentConfig;
import org.fortiss.pmwt.pertract.monitoring.agent.configuration.CassandraConnectionDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.PreparedStatement;

public class StacktraceCassandraCPUSamplingRepository extends CassandraCPUSamplingRepository {
	
	private static final Logger log = LoggerFactory.getLogger(StacktraceCassandraCPUSamplingRepository.class);

	public StacktraceCassandraCPUSamplingRepository(AgentConfig agentConfig) {
		super(agentConfig);
	}

	@Override
	public void createTableIfNotExists() {
		String statement = "CREATE TABLE IF NOT EXISTS " + CassandraConnectionDetails.keyspace + "." + CassandraConnectionDetails.tableStacktrace + " ("
				+ "experiment_name text, "
				+ "jvm text, "
				+ "stacktrace text, "
				+ "sample_time timestamp, "
				+ "cpu_time bigint, "
				+ "PRIMARY KEY ((experiment_name, jvm, stacktrace), sample_time)) "
				+ "WITH CLUSTERING ORDER BY (sample_time DESC)";
		this.session.execute(statement);
		log.info("Created table: \n" + statement);
	}

	/** Prepares the session for the old table */
	@Override
	public PreparedStatement prepareStatement() {
		String statement = "INSERT INTO " + CassandraConnectionDetails.tableStacktrace + " ("
				+ "experiment_name, jvm, stacktrace, sample_time, cpu_time) "
				+ "VALUES (?,?,?,?,?)";
		log.info("Prepare session with statement: \n" + statement);
		return this.session.prepare(statement);
	}

}
