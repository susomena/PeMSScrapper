package es.umh.uwicore.pems.scrapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jesús Mena-Oreja on 7/10/16.
 */
public class Handler {
	private final String BASE_URL = "http://pems.dot.ca.gov/";

	private String loginParams, cookie;

	public Handler(String username, String password){
		loginParams = "username=" + username + "&password=" + password;
	}

	public int login(){
		try {
			URL url = new URL(BASE_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			dos.writeBytes(loginParams);
			dos.flush();
			dos.close();

			String headerName;
			int responseCode = connection.getResponseCode();

			if (responseCode == 200) {
				for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
					if (headerName.equals("Set-Cookie")) {
						cookie = connection.getHeaderField(i);
						cookie = cookie.substring(0, cookie.indexOf(";"));
					}
				}
			}

			return responseCode;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int retrieveData(int id, int startEpoch, int endEpoch, String trafficVariable, String aggregationTime, ArrayList<Double> values){
		try {
			URL url = new URL(BASE_URL
					+ "?report_form=1&dnode=VDS&content=loops&tab=det_timeseries&export=text"
					+ "&station_id=" + id
					+ "&s_time_id=" + startEpoch
					+ "&e_time_id=" + endEpoch
					+ "&tod=all&tod_from=0&tod_to=0"
					+ "&dow_0=on&dow_1=on&dow_2=on&dow_3=on&dow_4=on&dow_5=on&dow_6=on&holidays=on"
					+ "&q=" + trafficVariable + "&q2="
					+ "&gn=" + aggregationTime + "&agg=on");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Cookie", cookie);

			int responseCode = connection.getResponseCode();

			if (responseCode == 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				String line = br.readLine(); // We don't need the first line

				while ((line = br.readLine()) != null) {
					String[] fields = line.split("\t");
					if(trafficVariable.equals("flow"))
						values.add(Double.parseDouble(fields[1].replace(",", "")) * 12.0 / Double.parseDouble(fields[2])); // Flow in veh/h/lane
					else
						values.add(Double.parseDouble(fields[1].replace(",", "")));
				}

				br.close();
			}

			return responseCode;
		} catch (IOException e){
			e.printStackTrace();
			return -1;
		}
	}
}
