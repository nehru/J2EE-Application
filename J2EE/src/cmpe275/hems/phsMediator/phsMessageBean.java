package cmpe275.hems.phsMediator;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.ejb.ActivationConfigProperty;

import cmpe275.hems.data.Request;
import cmpe275.hems.db.Log;
import cmpe275.hems.dbBean.LogBeanLocal;


@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "data/Request"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "10") })
		
public class phsMessageBean implements MessageListener{

	@EJB
	LogBeanLocal logbean;
	
	@Resource(mappedName="java:/XAConnectionFactory")
	private ConnectionFactory connectionFactory;
	
	@Resource(mappedName="data/Response")
	private Destination PhyReadQ;
	
	
	public void sendData(List<Log> qdata) throws JMSException {
		Connection connection = null;
		Session session = null;
		MessageProducer publisher = null;
		
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			//System.out.println("start*****");
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			 
			publisher = session.createProducer(PhyReadQ);
			ObjectMessage message = session.createObjectMessage();
			message.setObject((Serializable) qdata);
			publisher.send(message);
			//System.out.println("message sent*****");
		}
		finally {
			if (publisher != null) {
				publisher.close();
			}
			if (session != null) {
				session.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	
		
	}
	
	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		//Request ob = (Request) arg0;
		Request ob = null;
		try {
			if (arg0 instanceof ObjectMessage) {
				ob = (Request)(((ObjectMessage)arg0).getObject());
				
				System.out.println("mdb "+ob.getRegion()+" "+ob.getStime()+" "+ob.getEtime());
				
				List<Log> list= logbean.getLogs(ob.getStime(), ob.getEtime(), ob.getRegion());
		 		
				//System.out.println("Debug: mdb waiting to send data to phs client");
				Thread.sleep(1000);
				
			 
				
				//System.out.println("Debug: data sent from mdb");
				sendData(list);
				
				
			}
			else {
				System.out.println("Invalid message received by " + this.getClass().getSimpleName());
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		//System.out.println("Message received =" +ob.getRegion() + " "+ ob.getStime()+ " "+ ob.getEtime());
	}

}
