package org.fortiss.pmwt.pertract.monitoring.agent.configuration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AgentConfig {
	
	private String experimentName;
	private boolean storeStacktrace;
	private boolean storeThreadInfo;
	
	public AgentConfig() {
		this.experimentName = new SimpleDateFormat("yyyyMMdd-HH:mm").format(new Date());
		this.storeStacktrace = false;
		this.storeThreadInfo = true;
	}
	
	public void parseArgs(String concatenatedAgentArgs) {
		String[] agentArgs = concatenatedAgentArgs != null ? concatenatedAgentArgs.split(",") : null;
		if (agentArgs != null) {
			for (String agentArg : agentArgs) {
				String[] agentArgKeyValue = agentArg.split("=");
				if (agentArgKeyValue.length != 2) {
					throw new IllegalArgumentException("An illegal argument is passed to the Java agent");
				}
				
				String agentArgKey = agentArgKeyValue[0];
				String agentArgValue = agentArgKeyValue[1];
				if("experimentName".equalsIgnoreCase(agentArgKey)) {
					this.experimentName = agentArgValue;
				} else if ("stacktrace".equalsIgnoreCase(agentArgKey)) {
					this.storeStacktrace = parseBoolean(agentArgValue);
				} else if ("threadInfo".equalsIgnoreCase(agentArgKey)) {
					this.storeThreadInfo = parseBoolean(agentArgValue);
				}
			}
		}
	}
	
	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public boolean isStoreStacktrace() {
		return storeStacktrace;
	}

	public void setStoreStacktrace(boolean storeStacktrace) {
		this.storeStacktrace = storeStacktrace;
	}

	public boolean isStoreThreadInfo() {
		return storeThreadInfo;
	}

	public void setStoreThreadInfo(boolean storeThreadInfo) {
		this.storeThreadInfo = storeThreadInfo;
	}

	private static boolean parseBoolean(String value) {
		if (value != null) {
			if ("true".equalsIgnoreCase(value)) {
				return true;
			} else if ("false".equalsIgnoreCase(value)) {
				return false;
			}
		}
		
		throw new IllegalArgumentException("An illegal argument value is passed to the Java agent");
	}

}
