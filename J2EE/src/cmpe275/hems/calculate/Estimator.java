package cmpe275.hems.calculate;

import java.io.*;

import cmpe275.hems.data.OriginalData;

public interface Estimator extends Serializable{
	void estimate(OriginalData org);
}
