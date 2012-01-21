package cmpe275.hems.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ObjectMessage;

import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.jms.MessageProducer;

import cmpe275.hems.parameter.Param;
import cmpe275.hems.data.OriginalData;

public class DataCollector {

	private HashMap<String, ArrayList<OriginalData>> smsData;
	private ArrayList<OriginalData> sdata;

	private Timer timer101;
	private Timer timer880;
	private Timer timer680;
	private int inx101 = 1;
	private int inx880 = 1;
	private int inx680 = 1;

	private Context context;
	private ConnectionFactory connectionFactory;
	private Destination DataQueue101;
	private Destination DataQueue880;
	private Destination DataQueue680;

	public DataCollector() {
		timer101 = new Timer();
		timer880 = new Timer();
		timer680 = new Timer();
	}

	private OriginalData setupForData(String freeway) {
		OriginalData od = new OriginalData();
		od.setFreeway(freeway);
		return od;
	}

	public void initiateDataCollection() {
		final OriginalData rd101 = setupForData(Param.WAY101);
		final OriginalData rd880 = setupForData(Param.WAY880);
		final OriginalData rd680 = setupForData(Param.WAY680);

		timer101.schedule(new TimerTask() {
			public void run() {
				try {
					// System.out.println("sending 101 data");
					HashMap<String, ArrayList<OriginalData>> dt = DataExtraction(
							rd101.getFreeway(), inx101++);
					if (dt == null) {
						System.out.println("Error in data colleciton");
					} else {
						rd101.setCollectedData(dt);
						sendData(rd101);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, Param.DELAY, Param.PERIOD);

		timer880.schedule(new TimerTask() {
			public void run() {
				try {
					// System.out.println("sending 880 data");
					HashMap<String, ArrayList<OriginalData>> dt = DataExtraction(
							rd880.getFreeway(), inx880++);
					if (dt == null) {
						System.out.println("Error in data colleciton");
					} else {
						rd880.setCollectedData(dt);
						sendData(rd880);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, Param.DELAY, Param.PERIOD);

		timer680.schedule(new TimerTask() {
			public void run() {
				try {
					// System.out.println("sending 680 data");
					HashMap<String, ArrayList<OriginalData>> dt = DataExtraction(
							rd680.getFreeway(), inx680++);
					if (dt == null) {
						System.out.println("Error in data colleciton");
					} else {
						rd680.setCollectedData(dt);
						sendData(rd680);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, Param.DELAY, Param.PERIOD);

	}

	public void stopDataCollection() {
		timer101.cancel();
		timer880.cancel();
		timer680.cancel();
	}

	private Context getContext() throws Exception {
		if (context != null) {
			return context;
		}
		Properties pr = new Properties();
		pr.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		pr.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jnp.interfaces.NamingContextFactory");
		pr.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		context = new InitialContext(pr);
		return context;
	}

	private void queueSetup() throws Exception {
		Context ctx = getContext();
		Object ref = ctx.lookup("java:/XAConnectionFactory");
		connectionFactory = (ConnectionFactory) PortableRemoteObject.narrow(
				ref, ConnectionFactory.class);
		ref = ctx.lookup("queue/DataQueue101");
		DataQueue101 = (Destination) PortableRemoteObject.narrow(ref,
				Destination.class);
		ref = ctx.lookup("queue/DataQueue880");
		DataQueue880 = (Destination) PortableRemoteObject.narrow(ref,
				Destination.class);
		ref = ctx.lookup("queue/DataQueue680");
		DataQueue680 = (Destination) PortableRemoteObject.narrow(ref,
				Destination.class);

	}

	public synchronized void sendData(OriginalData orgData) throws Exception {
		Connection conn = null;
		Session sn = null;
		MessageProducer pub = null;

		try {
			queueSetup();
			conn = connectionFactory.createConnection();
			conn.start();
			sn = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

			if (orgData.getFreeway().equals("101")) {
				pub = sn.createProducer(DataQueue101);
				System.out.println("DataQueue101 sending data...");
			} else if (orgData.getFreeway().equals("880")) {
				pub = sn.createProducer(DataQueue880);
				System.out.println("DataQueue101 sending data...");
			} else if (orgData.getFreeway().equals("680")) {
				pub = sn.createProducer(DataQueue680);
				System.out.println("DataQueue101 sending data...");
			}

			System.out.println("sending data...");
			ObjectMessage msg = sn.createObjectMessage();
			msg.setObject(orgData);
			pub.send(msg);
			System.out.println("data sent to DataQueue101****");
		} finally {
			if (pub != null) {
				pub.close();
			}
			if (sn != null) {
				sn.close();
			}
			if (conn != null)
				conn.close();
		}

	}

	public synchronized HashMap<String, ArrayList<OriginalData>> DataExtraction(
			String whichRoad, int index) throws IOException {

		System.out.println("file name" + whichRoad);

		smsData = new HashMap<String, ArrayList<OriginalData>>();

		FileInputStream b_file = null;
		BufferedReader b_read = null;

		// Reading raw data from the freeway101.txt file

		try {

			String par;
			String file_name = new File("./").getCanonicalPath()
					+ "/build/classes/" + Param.DATA_LOCATION + "/" + "freeway"
					+ whichRoad + ".txt";
			System.out.println(file_name);

			b_file = new FileInputStream(file_name);
			b_read = new BufferedReader(new InputStreamReader(b_file));

			while ((par = b_read.readLine()) != null) {
				// System.out.println(par);
				sdata = new ArrayList<OriginalData>(Param.MATERIAL_COUNT);

				String[] sms = par.split(":");
				String sid = sms[0];

				// System.out.println(sms.length);
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

				// System.out.println("PARSING...."+co+" "+co2+" "+so2+" "+no+" "+part+" "+yr+" "+mn+" "+dy+" "+hr+" "+min+" "+sec);

				String tstamp = yr + "-" + mn + "-" + dy + "-T" + hr + ":"
						+ min + ":" + sec;

				/*
				 * OriginalData[] org = new OriginalData[5]; org[0] =
				 * (OriginalData) new OriginalData(sid, "co",co ,tstamp); org[1]
				 * = (OriginalData) new OriginalData(sid, "co2",co2 ,tstamp);
				 * org[2] = (OriginalData) new OriginalData(sid, "so2",so2
				 * ,tstamp); org[3] = (OriginalData) new OriginalData(sid,
				 * "no",no ,tstamp); org[4] = (OriginalData) new
				 * OriginalData(sid, "part",part ,tstamp);
				 */
				// sdata.add(org);
				sdata.add(new OriginalData(sid, "co", co, tstamp));
				sdata.add(new OriginalData(sid, "co2", co2, tstamp));
				sdata.add(new OriginalData(sid, "so2", so2, tstamp));
				sdata.add(new OriginalData(sid, "no", no, tstamp));
				sdata.add(new OriginalData(sid, "part", part, tstamp));

				// This data will be sent to jms queue
				smsData.put(sid, sdata);

			}
		}

		finally {
			if (b_read != null)
				b_read.close();

			if (b_file != null)
				b_file.close();
		}

		return smsData;
	}

}
