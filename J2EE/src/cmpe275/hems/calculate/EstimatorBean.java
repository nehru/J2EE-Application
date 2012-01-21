package cmpe275.hems.calculate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;


//import cmpe275.db.store.Log;
import cmpe275.hems.data.KmlPacket;
import cmpe275.hems.data.OriginalData;
import cmpe275.hems.datatransport.SendToCCCLocal;
import cmpe275.hems.db.Log;
import cmpe275.hems.dbBean.LogBeanLocal;
//import cmpe275.hems.db.Log;
//import cmpe275.hems.dbBean.LogBeanLocal;
//import cmpe275.hems.dbBean.LogBeanSession;
import cmpe275.hems.parameter.Param;



@Stateful
public class EstimatorBean implements EstimatorLocal, EstimatorRemote {
	
	private static final long serialVersionUID = -8876646787075138448L;
	
	@EJB
	SendToCCCLocal sendToCCC;
	
	@EJB
	LogBeanLocal logbean;	
	
	
	private Document kml_101;
	private Document kml_880;
	private Document kml_680;
	

	
	
	@PostConstruct
	public void readKMLFile(){
		//System.out.println("read kml file");
		try{
		SAXBuilder sb = new SAXBuilder(false); 	
		kml_101 = sb.build(this.getClass().getResourceAsStream("/rawData/"+Param.WAY101 +".kml"));
		kml_880 = sb.build(this.getClass().getResourceAsStream("/rawData/"+Param.WAY880 +".kml"));
		kml_680 = sb.build(this.getClass().getResourceAsStream("/rawData/"+Param.WAY680 +".kml"));
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void estimate(OriginalData org) {

		validateKML(org);
	
		HashMap<String,ArrayList<OriginalData>> smsData =org.getCollectedData();
		int i = 0;
 		for (String e : smsData.keySet()) {
				ArrayList<OriginalData> arr = smsData.get(e);
				
				
				for (OriginalData d : arr) {
				//	System.out.println("DEBUG ESTIMATOR BEAN"+org.getFreeway()+d.getSensorID()+" "+d.getMaterial()+" "+d.getValue()+" "+d.getTimeStamp());
				//Log ll = logbean.createLog(org.getFreeway(),d.getSensorID(), d.getMaterial(),d.getValue(),d.getTimeStamp());	
					
					Log ll = logbean.createLog(org.getFreeway(),d.getSensorID(), d.getMaterial(),d.getValue(),d.getTimeStamp());	
					
					
					if(ll == null)
						System.out.println("PROBLEMO!!!!!!!!!!!!!!!!!!!!!");	
						//System.out.println("DEBUGGGGGGGGGGGGINGGG ESTIMATOR BEAN"+d.getSensorID()+" "+d.getMaterial()+" "+d.getValue()+" "+d.getTimeStamp());				
				}	
				
			}

 		//To reterive data from jboss database
 		
 	/*	List<Log> list= logbean.getLogs("2009-02-12-T06:10:0","2009-02-12-T06:30:0");
 		for(Log e : list){
 			System.out.print(e.getFreeway()+"  "+e.getTimeStamp()+"  "+e.getId()+" "+e.getSensorId());  
				
 		}*/
 		
	}


	private void validateKML(OriginalData org) {
		Document doc = null;
		KmlPacket kl = null;
		
		//System.out.println("inside valdateKML ");
		
		
		if(org.getFreeway().equals(Param.WAY101))
		{
			doc = kml_101;
		}
		else if(org.getFreeway().equals(Param.WAY880))
		{
			doc = kml_880;
		}
		else if(org.getFreeway().equals(Param.WAY680))
		{
			doc = kml_680;
		}
		
		//System.out.println("doc "+ doc);	
		
		String kmlStyleUrl = "#sensor-clear";
		HashMap<String,ArrayList<OriginalData>> rData = org.getCollectedData();
		//System.out.println("material is " +rData.get("s-1").get(0).getMaterial());
		//System.out.println("material is " +rData.get("s-1").get(0).getFreeway());
		//System.out.println("material is " +rData.get("s-1").get(0).getTimeStamp());
		
		int count = 0;
		
			for (String sval : rData.keySet()) {
						
				//System.out.println("***Val = "+count++);
			ArrayList<OriginalData> alist = rData.get(sval);
						
			if(alist != null)
			{
				double co_Val = alist.get(Param.CO_INDEX).getValue();
				double co2_Val = alist.get(Param.CO2_INDEX).getValue();
				double so2_Val = alist.get(Param.SO2_INDEX).getValue();
				double no_Val = alist.get(Param.NO_INDEX).getValue();
				double part_Val = alist.get(Param.PART_INXEX).getValue();
				
				//System.out.println("EstimatorBean "+sval+" "+co_Val +" "+ co2_Val +" "+so2_Val +" "+no_Val+" "+ part_Val);
								
				if((co_Val == 0) && (co2_Val == 0)&& (so2_Val == 0)&& (no_Val == 0)&& (part_Val == 0))
				{
					kmlStyleUrl = "#sensor-offline";
				}
				else if((co_Val > Param.CO_MAX) && (co2_Val > Param.CO2_MAX)&& (so2_Val > Param.SO2_MAX)&& (no_Val > Param.NO_MAX)&& (part_Val > Param.PART_MAX))
				{
					kmlStyleUrl = "#sensor-congested";
				}
				//System.out.println("styleurl" + kmlStyleUrl);
			}
			
			Iterator itr = doc.getRootElement().getDescendants(new ElementFilter("Placemark"));
			while (itr.hasNext()) {
				Element mk = (Element)itr.next();
				String str = mk.getAttributeValue("id");
				
				//System.out.println("id*****" + str);
				
				if (str.equals(sval)) {
					//System.out.println("sensor id "+sval);
					Element styleUrl = (Element) mk.getDescendants(new ElementFilter("styleUrl")).next();
					styleUrl.setText(kmlStyleUrl);
					break;
				}
			}
			
			kl = new KmlPacket();
			kl.setFreeway(org.getFreeway());
			kl.setKml(doc);
			}
						
			try{
				//System.out.println("document prepared to send******");
				
				sendToCCC.sendKMLData(kl);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		
		
		
	}

	
	 

}
