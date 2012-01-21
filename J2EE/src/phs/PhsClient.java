package phs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

//import org.apache.commons.logging.Log;

import cmpe275.hems.data.OriginalData;
import cmpe275.hems.data.Request;
import cmpe275.hems.db.*;

public class PhsClient implements javax.jms.MessageListener{

	private Context ctx;
	private QueueSession session;
	private QueueSender sender;
	private File kDir;
	private Connection conn;
	private Session sn;
	private MessageConsumer user;
	
	private Log log;

	
	public void addMessage(String freeway, String st, String et) {
		try {
			if (freeway == null) {
				System.out.println("Missing region in message");
				return;
			}
			if (st == null) {
					System.out.println("Missing start time in message");
					return;
			}
					if (et == null) {
						System.out.println("Missing end time in message");
						return;
			}

			Request request = new Request();
			request.setRegion(freeway);
			request.setStime(st);
			request.setEtime(et);

			connectToQueue();
			ObjectMessage m = session.createObjectMessage(request);
			sender.send(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Context getContext() throws Exception {
		if (ctx != null)
			return ctx;

		Properties props = new Properties();
		props.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		props.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jnp.interfaces.NamingContextFactory");
		props.put(Context.URL_PKG_PREFIXES,
				"org.jboss.naming:org.jnp.interfaces");
		ctx = new InitialContext(props);

		return ctx;
	}
	
	private void connectToQueue() {
		if (sender != null)
			return;

		try {
			QueueConnection cnn = null;
			Context ctx = getContext();
			Queue queue = (Queue) ctx.lookup("data/Request");
			QueueConnectionFactory factory = (QueueConnectionFactory) ctx
					.lookup("ConnectionFactory");
			cnn = factory.createQueueConnection();
			session = cnn.createQueueSession(false,
					QueueSession.AUTO_ACKNOWLEDGE);
			sender = session.createSender(queue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
public void setupReadQueue(){
		
		//System.out.println("read txt file");
		try{
			
			
			kDir = new File("txt");
				if(!kDir.exists())
					kDir.mkdirs();
				
				Context ctex = getContext();
				Object ref = ctex.lookup("java:/XAConnectionFactory");
				
				ConnectionFactory cfy = (ConnectionFactory) PortableRemoteObject.narrow(ref,ConnectionFactory.class);
				ref = ctex.lookup("data/Response");
				Destination dt = (Destination) PortableRemoteObject.narrow(ref,Destination.class);
				
				conn = cfy.createConnection();
				sn = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
				user = sn.createConsumer(dt);
				user.setMessageListener(this);
				conn.start();
				System.out.println("conn started");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	
	
	
	
//**********************************************************	
		 
	public static void main(String[] args) {
		String fwy = "680";
		String st = "2009-02-12-T06:21:0";
		String et = "2009-02-12-T06:30:0";
		PhsClient ph = new PhsClient(); 
		
		//Setting up receive queue
		ph.setupReadQueue();
		
		//ph.addMessage("101", "2009-02-12-T06:10:0", "2009-02-12-T06:30:0");
		ph.addMessage(fwy, st, et);
		
		//System.out.println("Phy request " + fwy+ " data from "+ st+ " to " + et);
	}

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		Iterator itr;
		FileOutputStream fd = null;
		PrintWriter wr = null;
		
		try {
			if (arg0 instanceof ObjectMessage) {
				List<Log> data = (List<Log>)(((ObjectMessage)arg0).getObject());
				
				//System.out.println("Received Data is" + data);
				
			//	System.out.println("sensor "+data.get(0).getSensorId());
			//	System.out.println("mat "+data.get(0).getMaterial());
				
				itr = data.iterator();
				StringBuilder br = new StringBuilder();
				while(itr.hasNext()){
					Log tt = (Log) itr.next();
					//System.out.println("data:::"+tt.getFreeway()+" "+tt.getMaterial()+" "+tt.getSensorId()+" "+tt.getTimeStamp());
			       // out.write(tt.getFreeway()+"\t"+tt.getSensorId()+"\t"+tt.getMaterial()+"\t"+Double.toString(tt.getValue())+"\t"+tt.getTimeStamp()+"\n");
			        
					
					br.append(tt.getFreeway()).append("---").append(tt.getSensorId()).append("---").append(tt.getMaterial()).append("---").append(Double.toString(tt.getValue())).append(System.getProperty("line.separator"));
					
				}
				fd = new FileOutputStream(new File("File" + ".txt"));
				wr = new PrintWriter(fd);
				wr.write(br.toString());
				wr.flush();
			
				
			}
			else {
				System.out.println("Error receiving data");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if (fd != null) {
				try {
					fd.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (wr != null) {
				wr.close();
			}
		}

	}

}
