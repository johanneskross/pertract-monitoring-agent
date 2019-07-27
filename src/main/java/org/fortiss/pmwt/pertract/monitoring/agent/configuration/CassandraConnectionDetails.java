package org.fortiss.pmwt.pertract.monitoring.agent.configuration;

public class CassandraConnectionDetails {

	public static final String host 			= "192.168.22.93";
	public static final String keyspace			= "bd_monitoring";
	
	// The stacktrace is part of the primary key
	public static final String tableStacktrace	= "cpu_samples_stacktrace";
	// The concatenated thread group and thread name are part of the primary key
	public static final String tableThreadInfo 	= "cpu_samples_thread_info";

}
