package io.zeebe.postgres.exporter.pojos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import io.camunda.zeebe.protocol.record.Record;

public class DeploymentValue {
	private ArrayList<Object> resources;
	private ArrayList<ProcessesMetadata> processesMetadata;
	private ArrayList<Object> decisionRequirementsMetadata;
	private ArrayList<Object> decisionsMetadata;
	private ArrayList<Object> formMetadata;
	private String tenantId;

	public void save(Record record, Connection conn, long timestamp) {
		// TODO Auto-generated method stub
		PreparedStatement statement;
		// SimpleDateFormat formatter = new
		// SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSSS");
		Timestamp exportertimestamp = new Timestamp((new Date()).getTime());
		for (ProcessesMetadata pmi: processesMetadata) {
			String sql = "UPDATE postgres.deployments "
					+ " SET processdefinitionkey=?, resourcename=?, version=1+(SELECT version FROM postgres.deployments WHERE bpmnprocessid=?), exportertimestamp=?, updatedtimestamp=?"
					+ "	WHERE bpmnprocessid=?";

			try {
				// System.out.println("Attempting to update " + this.key + this.valueType);
				statement = conn.prepareStatement(sql);
				statement.setLong(1, pmi.getProcessDefinitionKey());
				statement.setString(2, pmi.getResourceName());
				statement.setString(3, pmi.getBpmnProcessId());
				statement.setTimestamp(4, exportertimestamp);
				statement.setTimestamp(5, new Timestamp(new Date(timestamp).getTime()));
				statement.setString(6, pmi.getBpmnProcessId());
				//System.out.println(statement.toString());
				int rowsUpdated = statement.executeUpdate();
				System.out.println("Total rows updated in deployments:" + rowsUpdated);
				if (rowsUpdated == 0) {
					throw new Exception("Nothing updated in deployments");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				sql = "INSERT INTO postgres.deployments ("
						+ "	bpmnprocessid, processdefinitionkey, resourcename, version, exportertimestamp, createdtimestamp)"
						+ "	VALUES (?, ?, ?, ?, ?, ?)";
				try {
					// System.out.println("Attempting to insert " + this.key + this.valueType);
					statement = conn.prepareStatement(sql);
					statement = conn.prepareStatement(sql);
					statement.setLong(2, pmi.getProcessDefinitionKey());
					statement.setString(3, pmi.getResourceName());
					statement.setLong(4, pmi.getVersion());
					statement.setTimestamp(5, exportertimestamp);
					statement.setTimestamp(6, new Timestamp(new Date(timestamp).getTime()));
					statement.setString(1, pmi.getBpmnProcessId());
					int rowsInserted = statement.executeUpdate();
					System.out.println("Total rows inserted in deployments:" + rowsInserted);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public ArrayList<Object> getResources() {
		return resources;
	}

	public void setResources(ArrayList<Object> resources) {
		this.resources = resources;
	}

	public ArrayList<ProcessesMetadata> getProcessesMetadata() {
		return processesMetadata;
	}

	public void setProcessMetadata(ArrayList<ProcessesMetadata> processesMetadata) {
		this.processesMetadata = processesMetadata;
	}

	public ArrayList<Object> getDecisionRequirementsMetadata() {
		return decisionRequirementsMetadata;
	}

	public void setDecisionRequirementsMetadata(ArrayList<Object> decisionRequirementsMetadata) {
		this.decisionRequirementsMetadata = decisionRequirementsMetadata;
	}

	public ArrayList<Object> getDecisionsMetadata() {
		return decisionsMetadata;
	}

	public void setDecisionsMetadata(ArrayList<Object> decisionsMetadata) {
		this.decisionsMetadata = decisionsMetadata;
	}

	public ArrayList<Object> getFormMetadata() {
		return formMetadata;
	}

	public void setFormMetadata(ArrayList<Object> formMetadata) {
		this.formMetadata = formMetadata;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
}
