package io.zeebe;

import jakarta.persistence.*;

@Entity
@Table(name = "records")
public class DemoEntity {

	private long id;
	private String record;
	public DemoEntity() {
		
	}
	
	public DemoEntity(String record) {
		this.record = record;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name = "record", nullable = false)
	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	

	@Override
	public String toString() {
		return "Employee [id=" + id + ", record=" + record
				+ "]";
	}
}
