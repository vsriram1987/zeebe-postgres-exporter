package io.zeebe.postgres.exporter.pojos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.ValueType;

public class VariableValue {
	private String tenantId;
	private String bpmnProcessId;
	private String name;
	private String value;
	private long processDefinitionKey;
	private long processInstanceKey;
	private long scopeKey;
	public void save(Record record, Connection conn) {
		// TODO Auto-generated method stub
		PreparedStatement statement;

		String sql = "UPDATE postgres.variables "
				+ " SET tenantid=?, bpmnprocessid=?, name=?, value=?, processdefinitionkey=?, processinstancekey=?, scopekey=?, status=?, exportertimestamp=?, updatedtimestamp=?"
				+ "	WHERE key=?";
		Timestamp exportertimestamp = new Timestamp((new Date()).getTime());
		try {
			statement = conn.prepareStatement(sql);
			statement.setLong(11, record.getKey());
			statement.setString(1, this.tenantId);
			statement.setString(2, this.bpmnProcessId);
			statement.setString(3, this.name);
			statement.setString(4, this.value);
			statement.setLong(5, this.processDefinitionKey);
			statement.setLong(6, this.processInstanceKey);
			statement.setLong(7, this.scopeKey);
			statement.setString(8, record.getIntent().toString());
			statement.setTimestamp(9, exportertimestamp);
			statement.setTimestamp(10, new Timestamp(new Date(record.getTimestamp()).getTime()));
			int rowsUpdated = statement.executeUpdate();
			System.out.println("Total rows updated in variables table:" + this.name + "=" + this.value + rowsUpdated);
			if (rowsUpdated == 0) {
				throw new Exception("Nothing updated");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			sql = "INSERT INTO postgres.variables("
					+ "	key, tenantid, bpmnprocessid, name, value, processdefinitionkey, processinstancekey, scopekey, status, exportertimestamp, createdtimestamp)"
					+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				statement = conn.prepareStatement(sql);
				statement.setLong(1, record.getKey());
				statement.setString(2, this.tenantId);
				statement.setString(3, this.bpmnProcessId);
				statement.setString(4, this.name);
				statement.setString(5, this.value);
				statement.setLong(6, this.processDefinitionKey);
				statement.setLong(7, this.processInstanceKey);
				statement.setLong(8, this.scopeKey);
				statement.setString(9, record.getIntent().toString());
				statement.setTimestamp(10, exportertimestamp);
				statement.setTimestamp(11, new Timestamp(new Date(record.getTimestamp()).getTime()));
				int rowsInserted = statement.executeUpdate();
				System.out.println("Total rows inserted in variables table:" + rowsInserted);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		updateVariablesHistory(this.name.replace("\"", "") + "=" + this.value.replace("\"",""), record, conn);
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getBpmnProcessId() {
		return bpmnProcessId;
	}
	public void setBpmnProcessId(String bpmnProcessId) {
		this.bpmnProcessId = bpmnProcessId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	public long getScopeKey() {
		return scopeKey;
	}
	public void setScopeKey(long scopeKey) {
		this.scopeKey = scopeKey;
	}
	public void updateVariablesHistory(String variables, Record record, Connection conn) {
		PreparedStatement statement;
		String sql = "";
		Timestamp exportertimestamp = new Timestamp((new Date()).getTime());
		variables = variables.replace("{", "").replace("}", "").replace(",", "\r\n");

		Properties properties = new Properties();
		try {
			properties.load(new ByteArrayInputStream(variables.getBytes(StandardCharsets.UTF_8)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Object key : properties.keySet()){

			// TODO Auto-generated catch block
			sql = "INSERT INTO postgres.variables_history("
					+ "	bpmnprocessid, name, value, processdefinitionkey, processinstancekey, activitytype, jobkey, createdtimestamp, exportertimestamp)"
					+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				statement = conn.prepareStatement(sql);
				statement.setString(1, this.bpmnProcessId);
				statement.setString(2, key.toString());
				statement.setString(3, properties.get(key).toString());
				statement.setLong(4, this.processDefinitionKey);
				statement.setLong(5, this.processInstanceKey);
				statement.setString(6, record.getValueType().name());
				statement.setLong(7, record.getKey());
				statement.setTimestamp(8, new Timestamp(new Date(record.getTimestamp()).getTime()));
				statement.setTimestamp(9, exportertimestamp);
				int rowsInserted = statement.executeUpdate();
				System.out.println("Total rows inserted in variables history table:" + rowsInserted);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
