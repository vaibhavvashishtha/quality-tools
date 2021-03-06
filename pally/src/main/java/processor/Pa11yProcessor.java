package processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by vaibhavvashishtha.
 */
public class Pa11yProcessor {
	
	private String resultFileName = "";
	private String resultDirectory = "";
	private String slash = "";
	
	public void processCSV(String resultFileName, String outputDirectoryForSplunkJsons, String propertiesFilePath,
			String propertiesFileName, String sourceCSV, String resultDirectory) {
		try {
			
			this.resultDirectory = resultDirectory;
			this.resultFileName = resultFileName;
			if (System.getProperty("os.name").startsWith("Windows")) {
				slash = "\\";
			} else {
				slash = "/";
			}
			writeUpdatedJson(readColumns(outputDirectoryForSplunkJsons + slash + sourceCSV));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Map<String, Object> readColumns(String csvFile) throws IOException  {
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = "";
		Map<String, Object> map = new HashMap<>();
		while ((line = br.readLine()) != null) {
			// use comma as separator
			String[] cols = line.split(",");
			cols[0] = cols[0].replaceAll("\"", "");
			if (map.containsKey(cols[0])) {
				map.put(cols[0], (Integer) map.get(cols[0]) + 1);
			} else {
				map.put(cols[0], 1);
			}
		}

		return map;
	}

	/**
	 * @param jsonData
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws Exception
	 */
	public void writeUpdatedJson(Map<String, Object> jsonData) throws JsonProcessingException, IOException  {
		ObjectMapper mapper = new ObjectMapper();
		FileOutputStream stream = new FileOutputStream(createFileIfNotPresent(), false);
		addDateToJson(jsonData);
		stream.write(mapper.writeValueAsString((jsonData)).getBytes());
		stream.close();
	}

	private void addDateToJson(Map<String, Object> targetJsonMap) {
		Calendar cal = Calendar.getInstance();
		targetJsonMap.put("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cal.getTime()));
	}
	
	private File createFileIfNotPresent() {
		File file = new File(resultDirectory + slash + resultFileName);
		file.getParentFile().mkdirs();
		return file;
	}

}
