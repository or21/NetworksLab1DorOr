import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class HeadRequest implements IClientRequest {
	
	private final String HTML = "html";
	private final String PATH_HTML = "html/";
	private final String PATH_IMAGE = "images/";
	private final String PATH_ICON = "icon/";
	private final String ICON = "ico";
	private final String TYPE_HTML = "text/html";
	private final String TYPE_ICON = "icon";
	private final String TYPE_IMAGE = "image/";
	
	protected final String HTTP_200_OK = "HTTP/1.1 200 OK\r\n";
	protected final String CRLF = "\r\n";
	protected final String m_StaticFilesPath = "static/";
	
	protected String m_Type;
	protected String m_Url;
	protected HashMap<String, String> m_Headers;
	protected byte[] m_Content;
	protected String m_ConfigFileRootPath;
	protected String m_ConfigFileDefaultPage;
	protected String m_Extension;
	protected Socket m_Socket;

	public HeadRequest(String[] i_FirstHeaderRow, HashMap<String, String> requestHeaders, Socket i_Socket) {
		this.m_Socket = i_Socket;
		m_ConfigFileRootPath = ConfigFile.GetInstance().GetConfigurationParameters().get(ConfigFile.CONFIG_FILE_ROOT_KEY);
		m_ConfigFileDefaultPage = ConfigFile.GetInstance().GetConfigurationParameters().get(ConfigFile.CONFIG_FILE_DEFAULT_PAGE_KEY);
		
		m_Url = i_FirstHeaderRow[1].replace("/../", "/");
		m_Url = i_FirstHeaderRow[1].replace("/..", "");

		if (m_Url.equals(m_ConfigFileRootPath)) {
			m_Type = TYPE_HTML;
		} else { 
			int i = m_Url.lastIndexOf('.');
			if (i > 0) {
				int substringTo = m_Url.contains("?") ? m_Url.indexOf("?") : m_Url.length();
				m_Extension = m_Url.substring(i + 1, substringTo);
				m_Type = (m_Extension.equals(HTML) || m_Extension.equals(m_ConfigFileRootPath)) ? TYPE_HTML :
					m_Extension.equals(ICON) ? TYPE_ICON : TYPE_IMAGE + m_Extension;
			}
		}
	}

	protected String createHeaders() {
		StringBuilder responseString = new StringBuilder(HTTP_200_OK);
		m_Headers = Tools.SetupResponseHeaders(m_Content, m_Type);

		for(String header : m_Headers.keySet()) {
			responseString.append(header).append(": ").append(m_Headers.get(header)).append(CRLF);
		}

		return responseString.toString();
	}
	
	protected File openFileAccordingToUrl(String i_Url) {
		return (m_Url.equals(m_ConfigFileRootPath) ? 
				new File(m_StaticFilesPath + PATH_HTML + m_ConfigFileDefaultPage) : 
					new File(m_StaticFilesPath + determineFileType() + m_Url));
	}
	
	private String determineFileType() {
		if (m_Type.equals(TYPE_HTML)) {
			return PATH_HTML;
		} else if (m_Type.equals(TYPE_ICON)) {
			return PATH_ICON;
		} else if (m_Type.startsWith(TYPE_IMAGE)) {
			return PATH_IMAGE;
		} else {
			throw new IllegalStateException("Requesting unknown filetypes for us to handle");
		}
	}

	@Override
	public void ReturnResponse() throws IOException {
		OutputStream outputStream = m_Socket.getOutputStream();
		File fileToReturn;
		fileToReturn = openFileAccordingToUrl(m_Url);
		if (!fileToReturn.exists()) {
			new NotFoundRequest(m_Socket).ReturnResponse();
		} else {
			m_Content = Tools.ReadFile(fileToReturn, m_Type);			
			String headersToReturn = createHeaders();
			
			System.out.println(headersToReturn);

			try {
				outputStream.write(headersToReturn.getBytes());
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
