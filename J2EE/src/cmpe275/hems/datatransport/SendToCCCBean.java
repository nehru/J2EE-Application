package cmpe275.hems.datatransport;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import cmpe275.hems.data.KmlPacket;
import cmpe275.hems.parameter.Param;

@Stateless
public class SendToCCCBean implements SendToCCCLocal,SendToCCCRemote{

	@Resource(mappedName="java:/XAConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName="queue/CCC101Queue")
	private Destination CCC101Queue;
	
	@Resource(mappedName="queue/CCC880Queue")
	private Destination CCC880Queue;
	
	@Resource(mappedName="queue/CCC680Queue")
	private Destination CCC680Queue;
	
	
	@Override
	public void sendKMLData(KmlPacket kp) throws Exception {
		MessageProducer pub = null;
		Connection conn = null;
		Session sn = null;
		
		//System.out.println("***** Receiveing kml file ******");
		
		try{
			conn = connectionFactory.createConnection();
			conn.start();
			sn = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			if(kp.getFreeway().equals(Param.WAY101))
			{
				pub = sn.createProducer(CCC101Queue);
			}
			else if(kp.getFreeway().equals(Param.WAY880)){
				pub = sn.createProducer(CCC880Queue);
			}
			else if(kp.getFreeway().equals(Param.WAY680)){
				pub = sn.createProducer(CCC680Queue);
			}
	
			ObjectMessage msg = sn.createObjectMessage();
			msg.setObject(kp);
			
			
			pub.send(msg);
			//System.out.println("***** Finished sending file to ccc client ******");
			
		}
		finally{
			if(pub != null)
				pub.close();
			if(conn != null)
				conn.close();
			if(sn != null)
				sn.close();
		} 
		
	}

}
