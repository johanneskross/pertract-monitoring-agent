# PerTract Monitoring Agent

* License: Eclipse Public License (EPL) - v 1.0

A Java agent to sample CPU traces with low overhead specialized for long running big data applications. By default, the sample interval is 100 milliseconds. The agent will collect cpu times for all active threads using the `java.lang.management.ThreadMXBean.getThreadCpuTime(long id)` method and store it to a Apache Cassandra database / keyspace. Note that the cpu sample values will increase steadily over time since we ignore captured samples from prior sample intevals and do not calculate the diffs due to performance reasons.

## Notice
This software is highly inspired by https://github.com/etsy/statsd-jvm-profiler

## Development Environment Setup

### Prerequirements
* Make sure you are running the latest Java Oracle JDK or OpenJDK
* Install Apache Cassandra and, possibly, enable remote access
* Install Eclipse (option 1)
  * Get Eclipse https://www.eclipse.org/downloads/eclipse-packages/   
  * Install m2e - Maven Integration for Eclipse
* Install Apache Maven (option 2)

### Build Agent from Source 
* Clone the git repository
* Configure Apache Cassandra connection
  * At the moment, the connection settings are hard coded in the source file
  * Open `/src/main/java/org/fortiss/pmwt/pertract/monitoring/agent/infrastructure/cassandra/CassandraCPUSamplingRepository.java`
  * Change the host ip address
* Eclipse (option 1)
  * Import the project in Eclipse
  * Project -> Run as -> Maven clean/install
* Apache Maven (option 2)
  * Use your terminal and change directory to project
  * Run 'mvn clean install'
* The compiled *.jar will be located in the target folder within the project folder

## Run Instructions
* For your Java applications
  * `java -javaagent:/<YOUR_PATH>/org.fortiss.pmwt.pertract.monitoring.agent.jar=experimentName=<EXPERIMENT_NAME>,stacktrace=<TRUE_OR_FALSE>,threadInfo=<TRUE_OR_FALSE> -jar <YOUR_APPLICATION>`
  * All of the Java agent options (`experimentName`, `stacktrace` and `threadInfo`) are optional. If some of them is not specified, their default values are:
  	 * `experimentName`: current date and time
  	 * `stacktrace`: true
  	 * `threadInfo`: false
* For Intel HiBench Benchmark (https://github.com/intel-hadoop/HiBench) open the `bin/functions/workload_functions.sh` script
  * For Apache Spark:
     * Search for `function run_spark_job()` and add the following to `SUBMIT_CMD="${SPARK_HOME}/bin/spark-submit`: <br />
     `--conf "spark.executor.extraJavaOptions=-javaagent:org.fortiss.pmwt.pertract.monitoring.agent.jar=<EXPERIMENT_NAME>" --jars /<YOUR_PATH>/org.fortiss.pmwt.pertract.monitoring.agent.jar"`
  * For Apache Flink:
     * Search for `function run-flink-job()` and add the following to `CMD="${FLINK_HOME}/bin/flink run`: <br />
     `-yDenv.java.opts=\"-javaagent:/<YOUR_PATH>/org.fortiss.pmwt.pertract.monitoring.agent.jar=<EXPERIMENT_NAME>\""`

## Analyze Results
* The measurement results will be stored in the keyspace *bd_monitoring*. If the *stacktrace* option of the Java agent is enabled, the table *cpu_samples_stacktrace* of your Apache Cassandra stores the measurement results, including a stacktrace. If the *threadInfo* option of the Java agent is enabled, the table *cpu_samples_thread_info* of your Apache Cassandra stores the measurement results, including thread information (i.e. thread group and thread name).
* Data model of the table *cpu_samples_stacktrace*
  * experiment_name (text)
  * jvm (text)
  * sample_time (timestamp)
  * stacktrace (text)
  * cpu_time (bigint)
* Data model of the table *cpu_samples_thread_info*
  * experiment_name (text)
  * jvm (text)
  * sample_time (timestamp)
  * thread_group_and_name (text)
  * cpu_time (bigint)
* You can also have a look at our simple *Monitoring UI* tool that provides a very simple Java(FX) application to visualize the cpu samples of both database tables

 
