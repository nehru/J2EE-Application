package cmpe275.hems.dbBean;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import cmpe275.hems.db.Log;




@Stateless
public class LogBeanSession implements LogBeanRemote, LogBeanLocal{
	@Resource
	private SessionContext ctx;
	
	@PersistenceContext(unitName = "cmpe275HemsProject")
	private EntityManager data;
	
	
	@Override
	public Log createLog(String freeway, String sensorId, String material,
			double value, String timeStamp) {
		// TODO Auto-generated method stub
		if (freeway == null || freeway.trim().length() == 0)
			throw new RuntimeException("freeway is missing");

		//System.out.println("createLog() request to create log: " + freeway);

		if (sensorId == null)
			sensorId = "Unknown";

		Log log = new Log();
		log.setFreeway(freeway);
		log.setSensorId(sensorId);
		log.setMaterial(material);
		log.setValue(value);
		log.setTimeStamp(timeStamp);
		data.persist(log);

		//System.out.println("*****createLog() request to create log: " + freeway+" _ "+sensorId);
		return log;
		
	}

	@Override
	public List<Log> getLogs(String st, String et, String fy) {
		// TODO Auto-generated method stub
		Query q= data.createNamedQuery("findLogs");
		q.setParameter(1, st);
		q.setParameter(2, et);
		q.setParameter(3, fy);
		
		//q.setParameter(1, "2009-02-12-T06:10:0");
		//q.setParameter(2, "2009-02-12-T06:30:0");
		
		List<Log> r = q.getResultList();

		return r;	
	}

}
