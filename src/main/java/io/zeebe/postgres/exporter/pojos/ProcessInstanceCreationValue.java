package io.zeebe.postgres.exporter.pojos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.ValueType;

public class ProcessInstanceCreationValue {
	private String bpmnProcessId;
	private long processDefinitionKey;
	private long processInstanceKey;
	private int version;
	private String variables;
	private ArrayList<Object> fetchVariables;
	private ArrayList<Object> startInstructions;
	private String tenantId;

	public String getBpmnProcessId() {
		return bpmnProcessId;
	}

	public void setBpmnProcessId(String bpmnProcessId) {
		this.bpmnProcessId = bpmnProcessId;
	}

	public long getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public void setProcessDefinitionKey(long processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public long getProcessInstanceKey() {
		return processInstanceKey;
	}

	public void setProcessInstanceKey(long processInstanceKey) {
		this.processInstanceKey = processInstanceKey;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getVariables() {
		return variables;
	}

	public void setVariables(Object variables) {
		if (variables != null) {
			this.variables = variables.toString();
		}
	}

	public ArrayList<Object> getFetchVariables() {
		return fetchVariables;
	}

	public void setFetchVariables(ArrayList<Object> fetchVariables) {
		this.fetchVariables = fetchVariables;
	}

	public ArrayList<Object> getStartInstructions() {
		return startInstructions;
	}

	public void setStartInstructions(ArrayList<Object> startInstructions) {
		this.startInstructions = startInstructions;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void save(Record record, Connection conn) {
		// TODO Auto-generated method stub
		PreparedStatement statement;

		String sql = "";
		Timestamp exportertimestamp = new Timestamp((new Date()).getTime());
		this.variables = this.variables.replace("{", "").replace("}", "").replace(",", "\r\n");

		Properties properties = new Properties();
		try {
			properties.load(new ByteArrayInputStream(this.variables.getBytes(StandardCharsets.UTF_8)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Object key : properties.keySet()){

			// TODO Auto-generated catch block
			sql = "INSERT INTO postgres.variables_history("
					+ "	bpmnprocessid, name, value, processdefinitionkey, processinstancekey, activitytype, createdtimestamp, exportertimestamp)"
					+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				statement = conn.prepareStatement(sql);
				statement.setString(1, this.bpmnProcessId);
				statement.setString(2, key.toString());
				statement.setString(3, properties.get(key).toString());
				statement.setLong(4, this.processDefinitionKey);
				statement.setLong(5, this.processInstanceKey);
				statement.setString(6, ValueType.PROCESS_INSTANCE_CREATION.name());
				statement.setTimestamp(7, new Timestamp(new Date(record.getTimestamp()).getTime()));
				statement.setTimestamp(8, exportertimestamp);
				int rowsInserted = statement.executeUpdate();
				System.out.println("Total rows inserted in variables history table:" + rowsInserted);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
