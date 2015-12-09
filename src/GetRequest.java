import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class GetRequest extends HeadRequest {
	
	protected HashMap<String, String> m_Params; 

	public GetRequest(String[] i_FirstHeaderRow, String[] i_AllOtherHeaders) {
		super(i_FirstHeaderRow, i_AllOtherHeaders);
		
		if (m_Url.contains("?")) {
			parseParams(m_Url.substring(m_Url.indexOf("?") + 1));
		}
	}

	protected void parseParams(String i_ParamsString) {
		String[] parametersArray = i_ParamsString.split("&");
		m_Params = new HashMap<String, String>();
		
		for (String string : parametersArray) {
			String[] keyValuePair = string.split("=");
			m_Params.put(keyValuePair[0], keyValuePair[1]);
		}
	}

	@Override
	public void ReturnResponse(OutputStream i_OutputStream) {
		File fileToReturn;
		fileToReturn = (m_Url.equals(ConfigFile.GetInstance().GetConfigurationParameters().get("root")) ? 
				new File(m_StaticFilesPath + "html/" + ConfigFile.GetInstance().GetConfigurationParameters().get("defaultPage")) : 
					new File(m_StaticFilesPath + "html/" + m_Url));
		if (!fileToReturn.exists()) {
			new NotFoundRequest().ReturnResponse(i_OutputStream);
		} else {
			m_Content = new String(Tools.ReadFile(fileToReturn, m_Type));			

			StringBuilder responseString = new StringBuilder(createHeaders());

			System.out.println(responseString);
			responseString.append("\r\n").append(m_Content);

			try {
				i_OutputStream.write(responseString.toString().getBytes());
				i_OutputStream.flush();
				i_OutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}