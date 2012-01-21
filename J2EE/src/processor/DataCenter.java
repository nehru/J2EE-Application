package processor;

import java.lang.*;
import cmpe275.hems.data.DataCollector;
import cmpe275.hems.parameter.Param;


public class DataCenter {

	private DataCollector dcol;
	
	
	public DataCenter(){
		dcol = new DataCollector();
	}
	
	public void startProcess(){
		dcol.initiateDataCollection();
	}
	
	public void stopProcess(){
		dcol.stopDataCollection();
	}
	
	public static void main(String[] args) {
		
		try{
			DataCenter dc = new DataCenter();
			System.out.println("Data collection system started...");
			dc.startProcess();
			System.out.println("This system will collect data upto " +Param.DELAY + " milli second");
			Thread.sleep(150000);
			dc.stopProcess();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
