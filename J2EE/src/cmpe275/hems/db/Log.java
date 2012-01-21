package cmpe275.hems.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@NamedQueries( {
@NamedQuery(name = "findLogs", query =  "SELECT e FROM Log e WHERE (e.timeStamp >= ?1 AND e.timeStamp <= ?2) AND e.freeway=?3"),	
	//@NamedQuery(name = "findLogs", query = "SELECT e FROM Log e ORDER BY e.sensorId"),
		@NamedQuery(name = "getLog", query = "SELECT e from Log e WHERE e.id = :id") })
@Table(name = "LOG25")
public class Log implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@Column(nullable = false, length = 64)
	protected String freeway;

	@Column(length = 64)
	protected String sensorId;

	@Column(nullable = false, length = 40)
	protected String material;
	
	@Column(nullable = false)
	protected double value;
	
	@Column(nullable = false, length = 512)
	protected String timeStamp;

	public Log(){
		//System.out.println("LOG file called");
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFreeway() {
		return freeway;
	}

	public void setFreeway(String freeway) {
		this.freeway = freeway;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	
}
