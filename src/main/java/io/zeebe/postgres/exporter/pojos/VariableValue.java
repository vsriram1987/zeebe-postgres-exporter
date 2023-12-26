package io.zeebe.postgres.exporter.pojos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import io.camunda.zeebe.protocol.record.Record;

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
			System.out.println("Total rows updated in variables table:" + rowsUpdated);
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
	
}
