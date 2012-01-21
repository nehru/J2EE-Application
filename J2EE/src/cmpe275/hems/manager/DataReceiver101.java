package cmpe275.hems.manager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.MessageDriven;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import cmpe275.hems.calculate.EstimatorLocal;
import cmpe275.hems.data.OriginalData;


@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/DataQueue101"),
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="acknowledgeMode", propertyValue="AUTO_ACKNOWLEDGE")
}, mappedName="queue/DataQueue101")

@EJB(name="test", beanInterface=EstimatorLocal.class)

public class DataReceiver101 implements javax.jms.MessageListener{
	@Resource 
	SessionContext ctext;
	EstimatorLocal est; 
	
	private HashMap<String,ArrayList<OriginalData>> smsData = null;
	private ArrayList<OriginalData> arr =  null;
	
	
	
	@PostConstruct
	public void init(){
		//System.out.println("init....");
		est = (EstimatorLocal)ctext.lookup("test");
		//if(est != null)
			//System.out.println("est lookup done");
	}
	
	
	
	@Override
	public void onMessage(Message arg0) {
		try {
			if (arg0 instanceof ObjectMessage) {
				OriginalData data = (OriginalData)(((ObjectMessage)arg0).getObject());
				
				est.estimate(data);
				
				//System.out.println("onMessage...");
				
				
				
				arr = data.getCollectedData().get("s-1");
				 
				//System.out.println("material " +arr.get(2).getMaterial());
				
				/*if(smsData != null)
					System.out.println("101 data received");
				else
					System.out.println("NO DATA RECEIVED");*/
			
			}
			else {
				System.out.println("Error receiving data");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
	
	
	
}
