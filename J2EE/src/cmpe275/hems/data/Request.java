package cmpe275.hems.data;

import java.io.Serializable;

public class Request implements Serializable {
	private String region;
	private String stime;
	private String etime;

	public String getRegion() {
		return region;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}

	
	public String getStime() {
		return stime;
	}
	
	public void setStime(String stime) {
		this.stime = stime;
	}

	public String getEtime() {
		return etime;
	}
	
	public void setEtime(String etime) {
		this.etime = etime;
	}
	
}
