import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class HeadRequest implements ClientRequest {
	
	private final String HTML = "html";
	private final String PATH_HTML = "html/";
	private final String PATH_IMAGE = "images/";
	private final String PATH_ICON = "icon/";
	private final String ICON = "ico";
	private final String TYPE_HTML = "text/html";
	private final String TYPE_ICON = "icon";
	private final String TYPE_IMAGE = "image/";
	
	protected final String HTTP_200_OK = "HTTP/1.1 200 OK\r\n";
	protected final String CLRF = "\r\n";
	
	protected String m_Type;
	protected String m_Url;
	protected final String m_StaticFilesPath = "static/";
	protected HashMap<String, String> m_Headers;
	protected String m_Content;
	protected String m_ConfigFileRootPath;
	protected String m_ConfigFileDefaultPage;
	protected String m_Extension;

	public HeadRequest(String[] i_FirstHeaderRow, String[] i_AllOtherHeaders) {
		m_ConfigFileRootPath = ConfigFile.GetInstance().GetConfigurationParameters().get(ConfigFile.CONFIG_FILE_ROOT_KEY);
		m_ConfigFileDefaultPage = ConfigFile.GetInstance().GetConfigurationParameters().get(ConfigFile.CONFIG_FILE_DEFAULT_PAGE_KEY);
		
		m_Url = i_FirstHeaderRow[1];

		if (m_Url.equals(m_ConfigFileRootPath)) {
			m_Type = TYPE_HTML;
		} else { 
			int i = m_Url.lastIndexOf('.');
			if (i > 0) {
				int substringTo = m_Url.contains("?") ? m_Url.indexOf("?") : m_Url.length();
				m_Extension = m_Url.substring(i + 1, substringTo);
				m_Type = m_Extension.equals(HTML) ? TYPE_HTML :
					m_Extension.equals(ICON) ? TYPE_ICON : // TODO: Dor. Place an icon, so that you can send it back, and not get a file not found...
						TYPE_IMAGE + m_Extension;
			}
		}
	}

	protected String createHeaders() {
		StringBuilder responseString = new StringBuilder(HTTP_200_OK);
		m_Headers = Tools.SetupHeaders(m_Content, m_Type);

		for(String header : m_Headers.keySet()) {
			responseString.append(header).append(": ").append(m_Headers.get(header)).append(CLRF);
		}

		return responseString.toString();
	}

	@Override
	public void ReturnResponse(OutputStream i_OutputStream) {
		// TODO Auto-generated method stub
		File fileToReturn;
		fileToReturn = (m_Url.equals(m_ConfigFileRootPath) ? 
				new File(m_StaticFilesPath + PATH_HTML + m_ConfigFileDefaultPage) : // TODO: This needs to change to deal with all filetypes 
					new File(m_StaticFilesPath + PATH_HTML + m_Url));
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
