package cmpe275.hems.dbBean;

import java.io.Serializable;
import java.util.List;

import cmpe275.hems.db.Log;

public interface LogBean extends Serializable{
	abstract List<Log> getLogs(String st,String et, String fwy);
	Log createLog(String freeway, String sensorId, String material, double value, String timeStamp);

}
