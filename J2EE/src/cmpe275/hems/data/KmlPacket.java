package cmpe275.hems.data;

import java.io.Serializable;

import org.jdom.Document;

public class KmlPacket implements Serializable{
	private String freeway;
	private Document kml;
	
	public KmlPacket(){}
	
	public String getFreeway() {
		return freeway;
	}
	public void setFreeway(String freeway) {
		this.freeway = freeway;
	}
	public Document getKml() {
		return kml;
	}
	public void setKml(Document kml) {
		this.kml = kml;
	}
	
	
}
