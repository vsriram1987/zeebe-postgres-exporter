package io.zeebe;

import io.camunda.zeebe.exporter.api.context.Context;
import io.camunda.zeebe.exporter.api.context.Controller;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import io.camunda.zeebe.exporter.api.Exporter;
import io.camunda.zeebe.protocol.record.Record;

public class DemoExporter implements Exporter {
    Controller controller;

    public void configure(Context context) throws Exception {
    	
    }

    public void open(Controller controller) {
        this.controller = controller;
    }

    public void close() {
    }

    public void export(Record record) {
        System.out.println("Test Message from exporter - " + record.toJson());
        this.controller.updateLastExportedRecordPosition(record.getPosition());
    }
}
