import java.io.FileReader;
import java.io.Reader;
import java.util.*;
import java.io.StringReader;
import java.io.FileNotFoundException;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.util.Iterator;

public class GiveMeTenCompanies {
	static final String USER_AGENT = "Mozilla/5.0";
	static final String LOCATION_TAG = "LocationTag";
	static final String MARKET_TAG = "MarketTag";
	static final String BASE_URL = "http://api.angel.co/1/";
	static final int MAX = 10;

	public static void main(String[] args){

		String tagID;
		StringBuffer builder = new StringBuffer();
		String getStartups;
		String startupString;
		JSONObject parsedObj = null;
		JSONArray startupArray;
		JSONObject startupObject;
		JSONArray locationsArray;
		JSONObject locationObject;
		JSONParser coParse = new JSONParser();

		JSONParser profileParser = new JSONParser();
		JSONObject profile = new JSONObject();

		// Check for candidate profile
		if(args.length > 0) {
			try{
				FileReader profileReader = new FileReader(args[0].toString());
				profile = (JSONObject) profileParser.parse(profileReader);
			}
			catch(Exception ex){
				System.out.println("ERROR: File could not be read");
				System.exit(1);
			}
		} else {
			System.out.println("ERROR: Did not provide a candidate file.");
			System.exit(2);
		}
		
		// Get candidate's interests
		Set tagIdSet = new HashSet(100);
		JSONArray marketTags = (JSONArray) profile.get("interests");
		if(marketTags != null){
			getTagIds(marketTags, MARKET_TAG, tagIdSet);
		}

		// Check locations only if you are willing to travel
		HashSet<String> locationIdSet = null;
		JSONArray locationTags = new JSONArray();
		locationTags.add(profile.get("location"));

		if(profile.get("travel").toString().equals("true")){
			locationTags.addAll((JSONArray)profile.get("locations"));

			locationIdSet = new HashSet(100);
			getTagIds(locationTags, LOCATION_TAG, locationIdSet);
		}
		
		// Start at 1 to make reading the list more human readable
		int count = 1;

		Iterator tagIter = tagIdSet.iterator();

		while( tagIter.hasNext() && count < 11 ) {
			tagID = tagIter.next().toString();

			getStartups = BASE_URL + "tags/" + tagID + "/startups?order=popularity&page=1&per_page=1";
			
			try{
				URL startupURL = new URL(getStartups);
				HttpURLConnection con = (HttpURLConnection) startupURL.openConnection();
				con.setRequestProperty("User-Agent", USER_AGENT);

				BufferedReader in = new BufferedReader(     new
				InputStreamReader(con.getInputStream())); String inputLine;	

				while ((inputLine = in.readLine()) != null) {
					builder.append(inputLine);
				}

				in.close();
				startupString = builder.toString();

				parsedObj = (JSONObject) coParse.parse(startupString);
				startupArray = (JSONArray) parsedObj.get("startups");
				Iterator it = startupArray.iterator();

				JSONObject idObj = (JSONObject) it.next();
				String id = idObj.get("id").toString();
				String name = idObj.get("name").toString();

				locationsArray = (JSONArray)idObj.get("locations");
				locationObject = (JSONObject) locationsArray.get(0);
				String locationID = locationObject.get("id").toString();
				String locationName = locationObject.get("name").toString();

				Iterator hashIt = locationIdSet.iterator();
				while(hashIt.hasNext()){
					if(hashIt.next().toString().equals(locationID)){

						System.out.println(count + ") " + name);
						System.out.println("Location: " + locationName + "\n");

						count++;
					}
				}
				builder.setLength(0);
			} 
			catch(Exception e){
				System.out.println("ERROR: " + e.getMessage());
				System.exit(2);
			}	
		}

		System.exit(0);
	}

	public static void getTagIds(JSONArray tagArray, final String type, Set tagSet) {
		// GET request sub-parts
		String searchQuery = "search?query=";
		String searchType = "&type=" + type;
		String request;
		String tags;
		StringBuffer builder = new StringBuffer();
		int count = 0;
		Object parsedObj = null;
		JSONObject currObj;
		JSONArray array;
		String currTag;
		Iterator<String> iterator = null;

		iterator = tagArray.iterator();

		while( iterator.hasNext() ) {
			//Build request for each market tag
			currTag = iterator.next();
			request = BASE_URL+searchQuery+currTag+searchType;

			try{
				URL obj = new URL(request);
				HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
				connection.setRequestProperty("User-Agent", USER_AGENT);

				BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					builder.append(inputLine);
				}

				in.close();
				tags = builder.toString();

				parsedObj = JSONValue.parse(tags);
				array = (JSONArray)parsedObj;

				while( count < array.size() && array.get(count) != null){
					currObj = (JSONObject) array.get(count);

					tagSet.add(currObj.get("id"));
					count++;					
				}

				// Reset count and string buffer
				count = 0;
				builder.setLength(0);
			}
			catch(Exception e){
				System.out.println("ERROR: " + e.getMessage());
				System.exit(2);
			}	
		}
	}
}