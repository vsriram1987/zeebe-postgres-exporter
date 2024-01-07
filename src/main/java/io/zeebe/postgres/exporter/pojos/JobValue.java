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

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.ValueType;
import io.camunda.zeebe.protocol.record.intent.JobIntent;

public class JobValue {
	@JsonIgnore
	private long key;
	private String tenantId;
	private String variables;
	private String bpmnProcessId;
	private String elementId;
	@JsonIgnore
	private String customHeaders;
	private int deadline;
	private int processDefinitionVersion;
	private String type;
	private String errorMessage;
	private String errorCode;
	private int retries;
	private int retryBackoff;
	private int recurringTime;
	private long processDefinitionKey;
	private long processInstanceKey;
	private long elementInstanceKey;
	private String worker;

	public void save(Record record, Connection conn) {
		// TODO Auto-generated method stub
		PreparedStatement statement;

		String sql = "UPDATE postgres.jobs "
				+ " SET tenantid=?, variables=?, bpmnprocessid=?, elementid=?, customheaders=?, deadline=?, processdefinitionversion=?, type=?, errormessage=?, errorcode=?, retries=?, retrybackoff=?, recurringtime=?, processdefinitionkey=?, processinstancekey=?, elementinstancekey=?, worker=?, status=?, exportertimestamp=?, updatedtimestamp=?, completedtimestamp=?"
				+ "	WHERE key=?";
		Timestamp exportertimestamp = new Timestamp((new Date()).getTime());
		try {
			statement = conn.prepareStatement(sql);
			statement.setLong(22, record.getKey());
			statement.setString(1, this.tenantId);
			statement.setString(2, this.variables.toString());
			statement.setString(3, this.bpmnProcessId);
			statement.setString(4, this.elementId);
			statement.setString(5, this.customHeaders);
			statement.setLong(6, this.deadline);
			statement.setLong(7, this.processDefinitionVersion);
			statement.setString(8, this.type);
			statement.setString(9, this.errorMessage);
			statement.setString(10, this.errorCode);
			statement.setLong(11, this.retries);
			statement.setLong(12, this.retryBackoff);
			statement.setLong(13, this.recurringTime);
			statement.setLong(14, this.processDefinitionKey);
			statement.setLong(15, this.processInstanceKey);
			statement.setLong(16, this.elementInstanceKey);
			statement.setString(17, this.worker);
			statement.setString(18, record.getIntent().toString());
			statement.setTimestamp(19, exportertimestamp);
			statement.setTimestamp(20, new Timestamp(new Date(record.getTimestamp()).getTime()));
			if (record.getIntent() == JobIntent.COMPLETED) {
				statement.setTimestamp(21, new Timestamp(new Date(record.getTimestamp()).getTime()));
			} else {
				statement.setTimestamp(21, null);
			}
			int rowsUpdated = statement.executeUpdate();
			System.out.println("Total rows updated in jobs table:" + rowsUpdated);
			this.updateVariablesHistory(record, conn);
			if (rowsUpdated == 0) {
				throw new Exception("Nothing updated");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			sql = "INSERT INTO postgres.jobs("
					+ "	key, tenantid, variables, bpmnprocessid, elementid, customheaders, deadline, processdefinitionversion, type, errormessage, errorcode, retries, retrybackoff, recurringtime, processdefinitionkey, processinstancekey, elementinstancekey, worker, status, exportertimestamp, createdtimestamp)"
					+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				statement = conn.prepareStatement(sql);
				statement.setLong(1, record.getKey());
				statement.setString(2, this.tenantId);
				statement.setString(3, this.variables.toString());
				statement.setString(4, this.bpmnProcessId);
				statement.setString(5, this.elementId);
				statement.setString(6, this.customHeaders);
				statement.setLong(7, this.deadline);
				statement.setLong(8, this.processDefinitionVersion);
				statement.setString(9, this.type);
				statement.setString(10, this.errorMessage);
				statement.setString(11, this.errorCode);
				statement.setLong(12, this.retries);
				statement.setLong(13, this.retryBackoff);
				statement.setLong(14, this.recurringTime);
				statement.setLong(15, this.processDefinitionKey);
				statement.setLong(16, this.processInstanceKey);
				statement.setLong(17, this.elementInstanceKey);
				statement.setString(18, this.worker);
				statement.setString(19, record.getIntent().toString());
				statement.setTimestamp(20, exportertimestamp);
				statement.setTimestamp(21, new Timestamp(new Date(record.getTimestamp()).getTime()));
				int rowsInserted = statement.executeUpdate();
				System.out.println("Total rows inserted in jobs table:" + rowsInserted);
				this.updateVariablesHistory(record, conn);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void setKey(long key) {
		this.key = key;
	}

	public long getKey() {
		return key;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getVariables() {
		return variables;
	}

	public void setVariables(Object variables) {
		if (variables != null) {
			this.variables = variables.toString();
		}
	}
	
	public void updateVariablesHistory(Record record, Connection conn) {
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
					+ "	bpmnprocessid, name, value, processdefinitionkey, processinstancekey, activitytype, jobkey, createdtimestamp, exportertimestamp)"
					+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				statement = conn.prepareStatement(sql);
				statement.setString(1, this.bpmnProcessId);
				statement.setString(2, key.toString());
				statement.setString(3, properties.get(key).toString());
				statement.setLong(4, this.processDefinitionKey);
				statement.setLong(5, this.processInstanceKey);
				statement.setString(6, ValueType.JOB.name());
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

	public String getBpmnProcessId() {
		return bpmnProcessId;
	}

	public void setBpmnProcessId(String bpmnProcessId) {
		this.bpmnProcessId = bpmnProcessId;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getCustomHeaders() {
		return customHeaders;
	}

	public void setCustomHeaders(String customHeaders) {
		this.customHeaders = customHeaders;
	}

	public int getDeadline() {
		return deadline;
	}

	public void setDeadline(int deadline) {
		this.deadline = deadline;
	}

	public int getProcessDefinitionVersion() {
		return processDefinitionVersion;
	}

	public void setProcessDefinitionVersion(int processDefinitionVersion) {
		this.processDefinitionVersion = processDefinitionVersion;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public int getRetryBackoff() {
		return retryBackoff;
	}

	public void setRetryBackoff(int retryBackoff) {
		this.retryBackoff = retryBackoff;
	}

	public int getRecurringTime() {
		return recurringTime;
	}

	public void setRecurringTime(int recurringTime) {
		this.recurringTime = recurringTime;
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

	public long getElementInstanceKey() {
		return elementInstanceKey;
	}

	public void setElementInstanceKey(long elementInstanceKey) {
		this.elementInstanceKey = elementInstanceKey;
	}

	public String getWorker() {
		return worker;
	}

	public void setWorker(String worker) {
		this.worker = worker;
	}
}
