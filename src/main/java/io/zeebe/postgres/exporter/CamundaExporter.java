package io.zeebe.postgres.exporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.camunda.zeebe.exporter.api.Exporter;
import io.camunda.zeebe.exporter.api.context.Context;
import io.camunda.zeebe.exporter.api.context.Controller;
import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.ValueType;
import io.camunda.zeebe.protocol.record.value.BpmnElementType;
import io.zeebe.postgres.exporter.pojos.EventRecord;
import io.zeebe.postgres.exporter.pojos.JobValue;
import io.zeebe.postgres.exporter.pojos.ProcessInstanceValue;
import io.zeebe.postgres.exporter.pojos.VariableValue;


public class CamundaExporter implements Exporter {
	Controller controller;
	Connection conn;

	
	public void configure(Context context) throws Exception {
		String dbURL = "jdbc:postgresql://localhost:5432/postgres";
		String username = "postgres";
		String password = "zeebe";
		context.setFilter(new ExporterFilter());

		try {

			conn = DriverManager.getConnection(dbURL, username, password);

			if (conn != null) {
				System.out.println("Connected");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void open(Controller controller) {
		this.controller = controller;
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void export(Record record) {
		//System.out.println("Test Message from exporter - " + record.toJson());
		this.controller.updateLastExportedRecordPosition(record.getPosition());
		
		// Export the entire record to recorddumper table
		/*
		 * CREATE TABLE IF NOT EXISTS postgres.recorddumper ( key character varying(255)
		 * COLLATE pg_catalog."default" NOT NULL, record character varying(2000) COLLATE
		 * pg_catalog."default", value character varying(2000) COLLATE
		 * pg_catalog."default", recordtype character varying(50) COLLATE
		 * pg_catalog."default", valuetype character varying(50) COLLATE
		 * pg_catalog."default", intent character varying(50) COLLATE
		 * pg_catalog."default", CONSTRAINT recorddumper_key PRIMARY KEY (key) )
		 */
		ObjectMapper om = new ObjectMapper();
		EventRecord er = new EventRecord();
		try {
			er = om.readValue(record.toJson(), EventRecord.class);
			//Value as string cannot be deserialized so added @jsonignore
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (record.getValueType()==ValueType.PROCESS_INSTANCE) {
			er.save(record, conn);
			ProcessInstanceValue piv = new ProcessInstanceValue();
			try {
				piv = om.readValue(record.getValue().toJson(), ProcessInstanceValue.class);
				//Value as string cannot be deserialized so added @jsonignore
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(piv.getBpmnElementType().equals(BpmnElementType.USER_TASK.name())) {
				// do nothing
			}
			else if(piv.getBpmnElementType().equals(BpmnElementType.PROCESS.name())) {
				piv.save(record, conn);
			}
			else {
				System.out.println("Process Instance Value not saved with BPMN Element type: " + piv.getBpmnElementType());
			}
		}
		else if (record.getValueType()==ValueType.PROCESS_INSTANCE_CREATION) {
			er.save(record, conn);
		}
		else if (record.getValueType()==ValueType.JOB) {
			er.save(record, conn);
			JobValue jv = new JobValue();
			try {
				jv = om.readValue(record.getValue().toJson(), JobValue.class);
				//Value as string cannot be deserialized so added @jsonignore
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jv.save(record, conn);
		}
		else if (record.getValueType()==ValueType.DEPLOYMENT) {
			er.save(record, conn);
		}
		else if (record.getValueType()==ValueType.VARIABLE) {
			er.save(record, conn);
			VariableValue vv = new VariableValue();
			try {
				vv = om.readValue(record.getValue().toJson(), VariableValue.class);
				//Value as string cannot be deserialized so added @jsonignore
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			vv.save(record, conn);
		}
		else {
			System.out.println("Record not saved with value type: " + record.getValueType());
		}
		}
		

		

	
	 
}
