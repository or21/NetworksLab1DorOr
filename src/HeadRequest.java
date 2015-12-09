import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class HeadRequest implements ClientRequest {

	protected String m_Type;
	protected String m_Url;
	protected final String m_StaticFilesPath = "static/";
	protected HashMap<String, String> m_Headers;
	protected String m_Content;

	public HeadRequest(String[] i_FirstHeaderRow, String[] i_AllOtherHeaders) {
		m_Url = i_FirstHeaderRow[1];

		if (m_Url.equals(ConfigFile.GetInstance().GetConfigurationParameters().get("root"))) {
			m_Type = "text/html";
		} else { 
			int i = m_Url.lastIndexOf('.');
			if (i > 0) {
				int substringTo = m_Url.contains("?") ? m_Url.indexOf('?') : m_Url.length();
				String extension = m_Url.substring(i + 1, substringTo);
				m_Type = extension.equals("html") ? "text/html" :
					extension.equals("ico") ? "icon" : // TODO: Dor. Place an icon, so that you can send it back, and not get a file not found...
						"image/" + extension;
			}
		}
	}

	protected String createHeaders() {
		StringBuilder responseString = new StringBuilder("HTTP/1.1 200 OK\r\n");
		m_Headers = Tools.SetupHeaders(m_Content, m_Type);

		for(String header : m_Headers.keySet()) {
			responseString.append(header).append(": ").append(m_Headers.get(header)).append("\r\n");
		}

		return responseString.toString();
	}

	@Override
	public void ReturnResponse(OutputStream i_OutputStream) {
		// TODO Auto-generated method stub
		File fileToReturn;
		fileToReturn = (m_Url.equals(ConfigFile.GetInstance().GetConfigurationParameters().get("root")) ? 
				new File(m_StaticFilesPath + "html/" + ConfigFile.GetInstance().GetConfigurationParameters().get("defaultPage")) : 
					new File(m_StaticFilesPath + "html/" + m_Url));
		if (!fileToReturn.exists()) {
			new NotFoundRequest().ReturnResponse(i_OutputStream);
		} else {
			m_Content = new String(Tools.ReadFile(fileToReturn, m_Type));			
			String headersToReturn = createHeaders();

			System.out.println(headersToReturn);

			try {
				i_OutputStream.write(headersToReturn.getBytes());
				i_OutputStream.flush();
				i_OutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
