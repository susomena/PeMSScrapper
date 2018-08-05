package es.umh.uwicore.pems;

import es.umh.uwicore.pems.scrapper.Handler;
import es.umh.uwicore.pems.scrapper.Scrapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Jesús Mena-Oreja on 7/10/16.
 */
public class Main {
	public static void main(String[] args) throws IOException, InterruptedException, ParseException{
		Handler handler = new Handler(args[0], args[1]);

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		int t1 = (int) (sdf.parse(args[2]).getTime() / 1000);
		int t2 = (int) (sdf.parse(args[3]).getTime() / 1000 + 24 * 3600 - 60);

		Scrapper[] scrappers = new Scrapper[args.length - 5];
		for(int i=0; i<scrappers.length; i++){
			long t = System.currentTimeMillis();
			System.out.println("Retrieving data for sensor " + args[5+i] + ":");
			scrappers[i] = new Scrapper(handler, Integer.parseInt(args[5+i]), args[4], t1, t2);
			scrappers[i].start();
			scrappers[i].join();
			System.out.println("Elapsed time: " + ((System.currentTimeMillis() - t) / 1000));
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter("dataset.csv"));
		for(int i=0; i<scrappers[0].getFlows().size(); i++){
			for(int j=0; j<scrappers.length; j++){
				bw.write("" + i + "," + j + "," + scrappers[j].getFlows().get(i) + "," + scrappers[j].getOccs().get(i) + "," + scrappers[j].getSpeeds().get(i));
				bw.newLine();
			}
		}
		bw.close();
	}
}
