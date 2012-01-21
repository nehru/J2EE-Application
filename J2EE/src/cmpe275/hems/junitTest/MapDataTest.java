package cmpe275.hems.junitTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import cmpe275.hems.data.DataCollector;
import cmpe275.hems.data.OriginalData;
import cmpe275.hems.parameter.Param;
import junit.framework.TestCase;




public class MapDataTest extends TestCase{
	private int counter = 1;
	//private String fway = "880";
	private OriginalData ord;
	private DataCollector dcol;
	private ArrayList<OriginalData> sdata;
	private HashMap<String,ArrayList<OriginalData>> smsData;
	
		
	
	protected void setUp() {
		
	}
	
	protected void tearDown() {
	}
	
	
	
	
	public synchronized HashMap<String, ArrayList<OriginalData>> DataExtraction(String whichRoad,int index) throws IOException {
 		
		//System.out.println("file name" + whichRoad);
		
	 		smsData = new HashMap<String,ArrayList<OriginalData>>();	
	 		
	 		FileInputStream b_file = null;
	 		BufferedReader b_read = null;
	 		
	 		//Reading raw data from the freeway101.txt file
	 		
	 		try{
	 			
	 			String par;
	 			String file_name = new File("./").getCanonicalPath()+"/build/classes/" + Param.DATA_LOCATION+"/"+"freeway" + whichRoad + ".txt";
	 			//System.out.println(file_name);
	 			
	 			b_file = new FileInputStream(file_name);
	 			b_read = new BufferedReader(new InputStreamReader(b_file));

	 			while ((par = b_read.readLine())!=null) {
	 			//System.out.println(par);
	 			sdata = new ArrayList<OriginalData>(Param.MATERIAL_COUNT);
		 				 			
	 			String[] sms = par.split(":");
	 			String sid = sms[0];
	 			
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
	 			
	 			//System.out.println(co+" "+co2+" "+so2+" "+no+" "+part+" "+yr+" "+mn+" "+dy+" "+hr+" "+min+" "+sec);
	 			
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

	
}
