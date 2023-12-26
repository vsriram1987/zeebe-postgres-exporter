package io.zeebe.postgres.exporter.pojos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.intent.ProcessInstanceIntent;


public class ProcessInstanceValue {
    private String tenantId;
    private String bpmnProcessId;
    private String elementId;
    private int parentProcessInstanceKey;
    private int parentElementInstanceKey;
    private int version;
    private long processDefinitionKey;
    private long processInstanceKey;
    private String bpmnElementType;
    private long flowScopeKey;
    private String bpmnEventType;
	public void save(Record record, Connection conn) {
		// TODO Auto-generated method stub
		PreparedStatement statement;

		String sql = "UPDATE postgres.processinstances "
				+ " SET tenantid=?, bpmnprocessid=?, elementid=?, parentprocessinstancekey=?, parentelementinstancekey=?, version=?, processdefinitionkey=?, bpmnelementtype=?, flowscopekey=?, bpmneventtype=?, status=?, exportertimestamp=?, updatedtimestamp=?, completedtimestamp=?"
				+ "	WHERE processinstancekey=?";
		Timestamp exportertimestamp = new Timestamp((new Date()).getTime());
		try {
			statement = conn.prepareStatement(sql);
			statement.setLong(15, this.processInstanceKey);
			statement.setString(1, this.tenantId);
			statement.setString(2, this.bpmnProcessId);
			statement.setString(3, this.elementId);
			statement.setLong(4, this.parentProcessInstanceKey);
			statement.setLong(5, this.parentElementInstanceKey);
			statement.setLong(6, this.version);
			statement.setLong(7, this.processDefinitionKey);
			statement.setString(8, this.bpmnElementType);
			statement.setLong(9, this.flowScopeKey);
			statement.setString(10, this.bpmnEventType);
			statement.setString(11, record.getIntent().toString());
			statement.setTimestamp(12, exportertimestamp);
			statement.setTimestamp(13, new Timestamp(new Date(record.getTimestamp()).getTime()));
			if (record.getIntent()==ProcessInstanceIntent.ELEMENT_COMPLETED) {
				statement.setTimestamp(14, new Timestamp(new Date(record.getTimestamp()).getTime()));
			}
			else {
				statement.setTimestamp(14,null);
			}
			//System.out.println(statement.toString());
			int rowsUpdated = statement.executeUpdate();
			System.out.println("Total rows updated in process instance table:" + rowsUpdated);
			if (rowsUpdated == 0) {
				throw new Exception("Nothing updated");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			sql = "INSERT INTO postgres.processinstances("
					+ "	processinstancekey, tenantid, bpmnprocessid, elementid, parentprocessinstancekey, parentelementinstancekey, version, processdefinitionkey, bpmnelementtype, flowscopekey, bpmneventtype, status, exportertimestamp, createdtimestamp)"
					+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				statement = conn.prepareStatement(sql);
				statement.setLong(1, this.processInstanceKey);
				statement.setString(2, this.tenantId);
				statement.setString(3, this.bpmnProcessId);
				statement.setString(4, this.elementId);
				statement.setLong(5, this.parentProcessInstanceKey);
				statement.setLong(6, this.parentElementInstanceKey);
				statement.setLong(7, this.version);
				statement.setLong(8, this.processDefinitionKey);
				statement.setString(9, this.bpmnElementType);
				statement.setLong(10, this.flowScopeKey);
				statement.setString(11, this.bpmnEventType);
				statement.setString(12, record.getIntent().toString());
				statement.setTimestamp(13, exportertimestamp);
				statement.setTimestamp(14, new Timestamp(new Date(record.getTimestamp()).getTime()));
				int rowsInserted = statement.executeUpdate();
				System.out.println("Total rows inserted in process instance table:" + rowsInserted);
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
	public String getElementId() {
		return elementId;
	}
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	public int getParentProcessInstanceKey() {
		return parentProcessInstanceKey;
	}
	public void setParentProcessInstanceKey(int parentProcessInstanceKey) {
		this.parentProcessInstanceKey = parentProcessInstanceKey;
	}
	public int getParentElementInstanceKey() {
		return parentElementInstanceKey;
	}
	public void setParentElementInstanceKey(int parentElementInstanceKey) {
		this.parentElementInstanceKey = parentElementInstanceKey;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
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
	public String getBpmnElementType() {
		return bpmnElementType;
	}
	public void setBpmnElementType(String bpmnElementType) {
		this.bpmnElementType = bpmnElementType;
	}
	public long getFlowScopeKey() {
		return flowScopeKey;
	}
	public void setFlowScopeKey(long flowScopeKey) {
		this.flowScopeKey = flowScopeKey;
	}
	public String getBpmnEventType() {
		return bpmnEventType;
	}
	public void setBpmnEventType(String bpmnEventType) {
		this.bpmnEventType = bpmnEventType;
	}
	
}
