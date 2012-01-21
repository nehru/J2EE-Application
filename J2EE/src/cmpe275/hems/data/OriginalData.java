package cmpe275.hems.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class OriginalData implements Serializable{
	
	private static final long serialVersionUID = 316262925200807072L;
	
	private HashMap<String,ArrayList<OriginalData>> collectedData;
	
	private String freeway;
	private String sensorID;
	private String material;
	private double value;
	private String timeStamp;
	
	public OriginalData(){
		
	}
	public OriginalData(String sid, String mat,double val,String time){
		this.sensorID = sid;
		this.material = mat;
		this.value = val;
		this.timeStamp = time;
		
	}
		
	public String getFreeway() {
		return freeway;
	}
	public void setFreeway(String fy) {
		this.freeway = fy;
	}
	
	public String getSensorID() {
		return sensorID;
	}
	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
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
	
	public void setCollectedData(HashMap<String,ArrayList<OriginalData>> cdata){
		this.collectedData = cdata;
	}
	
	public HashMap<String,ArrayList<OriginalData>> getCollectedData()
	{
		return this.collectedData;
	}
}
