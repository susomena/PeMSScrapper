package es.umh.uwicore.pems.scrapper;

import java.util.ArrayList;

/**
 * Created by Jesús Mena-Oreja on 11/10/16.
 */
public class Scrapper extends Thread {
	private Handler handler;
	private int detectorID, endEpoch;
	private int[] startEpochs;
	private String aggregationTime;

	private ArrayList<Double> flows, speeds, occs;

	public Scrapper(Handler handler, int detectorID, String aggregationTime, int startEpoch, int endEpoch){
		super();
		this.handler = handler;
		this.detectorID = detectorID;
		this.aggregationTime = aggregationTime;
		int numIterations = (endEpoch - startEpoch) / (7 * 24 * 3600);
		numIterations += (endEpoch - startEpoch) % (7 * 24 * 3600) == 0? 0 : 1;
		startEpochs = new int[numIterations];
		this.endEpoch = endEpoch;

		for(int i=0; i<numIterations; i++){
			startEpochs[i] = startEpoch + i * 7 * 24 * 3600;
		}

		flows = new ArrayList<>();
		speeds = new ArrayList<>();
		occs = new ArrayList<>();
	}

	@Override
	public void run() {
		for(int startEpoch : startEpochs){
			System.out.println("\tEpoch: " + startEpoch);
			handler.login();
			if(endEpoch - startEpoch > 7 * 24 * 3600 - 60) {
				handler.retrieveData(detectorID, startEpoch, startEpoch + (7 * 24 * 3600 - 60), "flow", aggregationTime, flows);
				handler.retrieveData(detectorID, startEpoch, startEpoch + (7 * 24 * 3600 - 60), "speed", aggregationTime, speeds);
				handler.retrieveData(detectorID, startEpoch, startEpoch + (7 * 24 * 3600 - 60), "occ", aggregationTime, occs);
			} else{
				handler.retrieveData(detectorID, startEpoch, endEpoch, "flow", aggregationTime, flows);
				handler.retrieveData(detectorID, startEpoch, endEpoch, "speed", aggregationTime, speeds);
				handler.retrieveData(detectorID, startEpoch, endEpoch, "occ", aggregationTime, occs);
			}
		}
	}

	public ArrayList<Double> getFlows() {
		return flows;
	}

	public ArrayList<Double> getSpeeds() {
		return speeds;
	}

	public ArrayList<Double> getOccs() {
		return occs;
	}
}
