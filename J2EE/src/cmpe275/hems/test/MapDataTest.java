package cmpe275.hems.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import ccc.Receive101UpdatedKML;
import cmpe275.hems.data.DataCollector;
import cmpe275.hems.data.OriginalData;
import cmpe275.hems.parameter.Param;
import junit.framework.TestCase;

public class MapDataTest extends TestCase{
	private int counter = 1;
	private String fway;
	private OriginalData ord;
	private DataCollector dcol;
	private ArrayList<OriginalData> sdata;
	private HashMap<String,ArrayList<OriginalData>> smsData;
	
		
	
	protected void setUp() {
		//System.out.println("setup.......");
		
		fway = "101";
		ord = new OriginalData();
		ord.setFreeway(fway);
		dcol = new DataCollector();	
	}
	
	protected void tearDown() {
	}
	
	public void testHemsData()
	{
		Receive101UpdatedKML rv = null;
		
		//System.out.println("dataflowtest.......");
		try{
			
			rv = new Receive101UpdatedKML();
			rv.readFile();
			
			//CCC waiting to receive kml file data from the data manager
			
			Thread.sleep(5000);
			rfile();
			HashMap<String, ArrayList<OriginalData>> da = DataExtraction("freewayTestData.txt",counter);
			if(da == null){
				System.out.println("Error: in parsing");
				return;
			}
			else
			{
				ord.setCollectedData(da);
				dcol.sendData(ord);
			}
			
			Thread.sleep(5000);
			String st = retUrl("s-3");
			System.out.println("style is "+st);
			
			assertEquals("#sensor-clear", retUrl("s-1"));
			assertEquals("#sensor-offline", retUrl("s-11"));
			assertEquals("#sensor-clear", retUrl("s-15"));
			assertEquals("#sensor-congested", retUrl("s-19"));
			
						
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rv != null)
			{
				rv.cleanup();
			}
		}
		System.out.println("testing done");
	}
	
	
	public String retUrl(String sval)
	{
		try{
			SAXBuilder bdr = new SAXBuilder(false);
			File fd = new File(new File("./").getCanonicalPath()+"/"+ "visual");
			File fl[] = fd.listFiles();
			if(fl.length != 0 && fl[0].isFile())
			{
				Document fdoc = bdr.build(fl[0]);
				Iterator itr = fdoc.getRootElement().getDescendants(new ElementFilter("Placemark"));
				while(itr.hasNext())
				{
					Element pmark = (Element)itr.next();
					String id = pmark.getAttributeValue("id");
					if(id.equals(sval)){
						Element url = (Element)pmark.getDescendants(new ElementFilter("styleUrl")).next();
						return url.getText();
					}
				}
			
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public HashMap<String, ArrayList<OriginalData>> DataExtraction(String fname,int index) throws IOException {
 		
		System.out.println("file name" + fname);
		
	 		smsData = new HashMap<String,ArrayList<OriginalData>>();	
	 		
	 		FileInputStream b_file = null;
	 		BufferedReader b_read = null;
	 		
	 		//Reading raw data from the freeway101.txt file
	 		
	 		try{
	 			
	 			String par;
	 			String file_name = new File("./").getCanonicalPath()+"/build/classes/" + Param.DATA_LOCATION+"/"+fname;
	 			//System.out.println("file name is ****"+file_name);
	 			
	 			b_file = new FileInputStream(file_name);
	 			b_read = new BufferedReader(new InputStreamReader(b_file));

	 			while ((par = b_read.readLine())!=null) {
	 			//System.out.println(par);
	 			sdata = new ArrayList<OriginalData>(Param.MATERIAL_COUNT);
		 				 			
	 			String[] sms = par.split(":");
	 			String sid = sms[0];
	 			
	 			System.out.println(sid);	
	 			
	 			//System.out.println(sms.length);	
	 			String[] ind = sms[index].split(",");
	 				 			
	 			double co = Double.parseDouble(ind[Param.CO_OFF]);
	 			double co2 = Double.parseDouble(ind[Param.C02_OFF]);
	 			double so2 = Double.parseDouble(ind[Param.SO2_OFF]);
	 			double no = Double.parseDouble(ind[Param.NO_OFF]);
	 			double part = Double.parseDouble(ind[Param.PART_OFF]);
	 			
	 			String yr = ind[Param.YEAR_OFF];
	 			String mn = ind[Param.MONTH_OFF];
	 			String dy = ind[Param.DAY_OFF];
	 			String hr = ind[Param.HOUR_OFF];
	 			String min = ind[Param.MIN_OFF];
	 			String sec = ind[Param.SEC_OFF];
	 			
	 			System.out.println("+Testing parsing"+co+" "+co2+" "+so2+" "+no+" "+part+" "+yr+" "+mn+" "+dy+" "+hr+" "+min+" "+sec);
	 			
	 			 String tstamp = yr+"-"+mn+"-"+dy+"-T"+hr+":"+min+":"+sec;

	 			
	 		/*	OriginalData[] org = new OriginalData[5];
	 			org[0] = (OriginalData) new OriginalData(sid, "co",co ,tstamp);
	 			org[1] = (OriginalData) new OriginalData(sid, "co2",co2 ,tstamp);
	 			org[2] = (OriginalData) new OriginalData(sid, "so2",so2 ,tstamp);
	 			org[3] = (OriginalData) new OriginalData(sid, "no",no ,tstamp);
	 			org[4] = (OriginalData) new OriginalData(sid, "part",part ,tstamp);
	 			*/
	 			//sdata.add(org);
	 			sdata.add(new OriginalData(sid, "co",co ,tstamp));
	 			sdata.add( new OriginalData(sid, "co2",co2 ,tstamp));
	 			sdata.add( new OriginalData(sid, "so2",so2 ,tstamp));
	 			sdata.add( new OriginalData(sid, "no",no ,tstamp));
	 			sdata.add( new OriginalData(sid, "part",part ,tstamp));

	 			//This data will be sent to jms queue
	 			smsData.put(sid,sdata);
 			
	 			}
	 		}
	 		
	 		finally{
	 			if(b_read != null)
	 				b_read.close();
	 			
	 			if(b_file != null)
	 				b_file.close();
	 		}
	 			
	 		
	 		
	 		return smsData;
	}

	
	
	
	
	private void rfile(){
		int x;
		try{
			File fd = new File("visual");
			File fl[] = fd.listFiles();
			for(x=0;x<fl.length;x++)
				fl[x].delete();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
