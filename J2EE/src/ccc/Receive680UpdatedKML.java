package ccc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.jdom.output.XMLOutputter;

import cmpe275.hems.data.KmlPacket;
import cmpe275.hems.parameter.Param;

public class Receive680UpdatedKML implements javax.jms.MessageListener{

	private Context ctex;
	private Connection conn;
	private Session sn;
	private MessageConsumer user;
	private File kDir;
	private long counter = 0;
	
	private Context getContext() throws Exception{
		if(ctex != null)
			return ctex;
		Properties ps = new Properties();
		ps.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		ps.put(Context.INITIAL_CONTEXT_FACTORY,"org.jnp.interfaces.NamingContextFactory");
		ps.put(Context.URL_PKG_PREFIXES,"org.jboss.naming:org.jnp.interfaces");
		ctex = new InitialContext(ps);
		return ctex;
	}
	
	public void readFile(){
		try{
				kDir = new File("visual");
				if(!kDir.exists())
					kDir.mkdirs();
				
				Context ctex = getContext();
				Object ref = ctex.lookup("java:/XAConnectionFactory");
				
				ConnectionFactory cfy = (ConnectionFactory) PortableRemoteObject.narrow(ref,ConnectionFactory.class);
				ref = ctex.lookup("queue/CCC680Queue");
				Destination dt = (Destination) PortableRemoteObject.narrow(ref,Destination.class);
				
				conn = cfy.createConnection();
				sn = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
				user = sn.createConsumer(dt);
				user.setMessageListener(this);
				conn.start();
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	
	
	public void finalize(){
		if(user != null)
			try {
				user.close();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(sn != null)
				try {
					sn.close();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(conn != null)
					try {
						conn.close();
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	}
	
	
	public static void main(String[] args) {
		 
		Receive680UpdatedKML rv = new Receive680UpdatedKML();
		rv.readFile();
		System.out.println("CCC Freeway " + Param.WAY680 + " listener started successfully");
		
		
	}

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		try{
			if(arg0 instanceof ObjectMessage){
				KmlPacket pac = (KmlPacket)((ObjectMessage)arg0).getObject();
				FileOutputStream out = null;
				
				//System.out.println("ccc on message received kml file");
				
				try {
					long lvar = ++counter;
					out = new FileOutputStream(new File(kDir,"Map"+pac.getFreeway() + "-"+ lvar + ".kml"));
					new XMLOutputter().output(pac.getKml(), out);
					//System.out.println("File " +"Map"+pac.getFreeway() + "-"+ lvar + ".kml"	+ " KML file written to disk");
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (out != null) {
						out.close();
					}
				}
							
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
	}

}
