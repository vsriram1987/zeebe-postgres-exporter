package io.zeebe.postgres.exporter.pojos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.camunda.zeebe.protocol.record.Record;

public class EventRecord {
	private int partitionId;
	@JsonIgnore
	private String value;
	private String brokerVersion;
	private int position;
	private long key;
	private long timestamp;
	private String valueType;
	private int sourceRecordPosition;
	private String intent;
	private String recordType;
	private String rejectionType;
	private String rejectionReason;
	private Authorizations authorizations;
	private int recordVersion;

	public void save(Record record, Connection conn) {
		// TODO Auto-generated method stub
		this.value = record.getValue().toString();
		PreparedStatement statement;
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSSS");
		Timestamp exportertimestamp = new Timestamp((new Date()).getTime());
		String sql = "UPDATE postgres.events "
				+ " SET partitionid=?, value=?, brokerversion=?, \"position\"=?, valuetype=?, sourcerecordposition=?, intent=?, recordtype=?, rejectiontype=?, rejectionreason=?, authorizations=?, recordversion=?, exportertimestamp=?, updatedtimestamp=?"
				+ "	WHERE key=? and valuetype=?";

		try {
			//System.out.println("Attempting to update " + this.key + this.valueType);
			statement = conn.prepareStatement(sql);
			statement.setLong(15, this.key);
			statement.setString(16, this.valueType);
			statement.setLong(1, this.partitionId);
			statement.setString(2, this.value);
			statement.setString(3, this.brokerVersion);
			statement.setLong(4, this.position);
			statement.setString(5, this.valueType);
			statement.setLong(6, this.sourceRecordPosition);
			statement.setString(7, this.intent);
			statement.setString(8, this.recordType);
			statement.setString(9, this.rejectionType);
			statement.setString(10, this.rejectionReason);
			statement.setString(11, this.authorizations.toString());
			statement.setLong(12, this.recordVersion);
			statement.setTimestamp(13, exportertimestamp);
			statement.setTimestamp(14, new Timestamp(new Date(this.timestamp).getTime()));
			//System.out.println(statement.toString());
			int rowsUpdated = statement.executeUpdate();
			System.out.println("Total rows updated in event recorder:" + rowsUpdated);
			if (rowsUpdated == 0) {
				throw new Exception("Nothing updated in event recorder");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			sql = "INSERT INTO postgres.events("
					+ "	key, partitionid, value, brokerversion, \"position\", valuetype, sourcerecordposition, intent, recordtype, rejectiontype, rejectionreason, authorizations, recordversion, exportertimestamp, createdtimestamp)"
					+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				//System.out.println("Attempting to insert " + this.key + this.valueType);
				statement = conn.prepareStatement(sql);
				statement.setLong(1, this.key);
				statement.setLong(2, this.partitionId);
				statement.setString(3, this.value);
				statement.setString(4, this.brokerVersion);
				statement.setLong(5, this.position);
				statement.setString(6, this.valueType);
				statement.setLong(7, this.sourceRecordPosition);
				statement.setString(8, this.intent);
				statement.setString(9, this.recordType);
				statement.setString(10, this.rejectionType);
				statement.setString(11, this.rejectionReason);
				statement.setString(12, this.authorizations.toString());
				statement.setLong(13, this.recordVersion);
				statement.setTimestamp(14, exportertimestamp);
				statement.setTimestamp(15, new Timestamp(new Date(this.timestamp).getTime()));;
				int rowsInserted = statement.executeUpdate();
				System.out.println("Total rows inserted in event recorder:" + rowsInserted);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public int getPartitionId() {
		return partitionId;
	}

	public void setPartitionId(int partitionId) {
		this.partitionId = partitionId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getBrokerVersion() {
		return brokerVersion;
	}

	public void setBrokerVersion(String brokerVersion) {
		this.brokerVersion = brokerVersion;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public int getSourceRecordPosition() {
		return sourceRecordPosition;
	}

	public void setSourceRecordPosition(int sourceRecordPosition) {
		this.sourceRecordPosition = sourceRecordPosition;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getRejectionType() {
		return rejectionType;
	}

	public void setRejectionType(String rejectionType) {
		this.rejectionType = rejectionType;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public Authorizations getAuthorizations() {
		return authorizations;
	}

	public void setAuthorizations(Authorizations authorizations) {
		this.authorizations = authorizations;
	}

	public int getRecordVersion() {
		return recordVersion;
	}

	public void setRecordVersion(int recordVersion) {
		this.recordVersion = recordVersion;
	}

}
