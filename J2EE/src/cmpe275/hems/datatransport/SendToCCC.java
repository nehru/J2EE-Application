package cmpe275.hems.datatransport;

import java.io.*;

import cmpe275.hems.data.KmlPacket;

public interface SendToCCC extends Serializable{
	void sendKMLData(KmlPacket kp) throws Exception;;
}
