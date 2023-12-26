package io.zeebe.postgres.exporter;
import io.camunda.zeebe.exporter.api.context.Context;
import io.camunda.zeebe.protocol.record.RecordType;
import io.camunda.zeebe.protocol.record.ValueType;

public class ExporterFilter implements Context.RecordFilter {

    @Override
    public boolean acceptType(RecordType recordType) {
    	
        return recordType.equals(RecordType.EVENT);
    	//return true;
    }

    @Override
    public boolean acceptValue(ValueType valueType) {
        		return valueType.equals(ValueType.JOB) || 
        				valueType.equals(ValueType.PROCESS_INSTANCE) ||
        				valueType.equals(ValueType.PROCESS_INSTANCE_CREATION) ||
        				valueType.equals(ValueType.PROCESS_INSTANCE_MODIFICATION) ||
        				valueType.equals(ValueType.PROCESS_INSTANCE_RESULT) ||
        				valueType.equals(ValueType.DEPLOYMENT) ||
        				valueType.equals(ValueType.VARIABLE);
    	//return true;
    }
 
}

