import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class HeadRequest implements IClientRequest {

	private final String HTML = "html";
	private final String JPG = "jpg";
	private final String GIF = "gif";
	private final String PNG = "png";
	private final String BMP = "bmp";
	private final String PATH_HTML = "html/";
	private final String PATH_IMAGE = "images/";
	private final String PATH_ICON = "icon/";
	private final String PATH_DEFAULT = "default/";
	private final String ICON = "ico";
	private final String TYPE_HTML = "text/html";
	private final String TYPE_ICON = "icon";
	private final String TYPE_IMAGE = "image/";
	private final String TYPE_OCTET = "application/octet";

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
				m_Type = determineType(m_Extension); 
			}
		}
	}

	private String determineType(String i_Extension) {
		if(i_Extension.equals(HTML)) {
			return TYPE_HTML;
		} else if (i_Extension.equals(ICON)) {
			return TYPE_ICON;
		} else if (i_Extension.equals(JPG)) {
			return TYPE_IMAGE + JPG;
		} else if (i_Extension.equals(BMP)) {
			return TYPE_IMAGE + BMP;
		} else if (i_Extension.equals(GIF)) {
			return TYPE_IMAGE + GIF;
		} else if (i_Extension.equals(PNG)) {
			return TYPE_IMAGE + PNG;
		} else {
			return TYPE_OCTET;
		}
	}

	protected String createHeaders() {
		StringBuilder responseString = new StringBuilder(HTTP_200_OK);

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
			return PATH_DEFAULT;
		}
	}

	@Override
	public void ReturnResponse() throws IOException {
		File fileToReturn;
		fileToReturn = openFileAccordingToUrl(m_Url);

		if (!fileToReturn.exists()) {
			new NotFoundRequest(m_Socket).ReturnResponse();
		} else {
			OutputStream outputStream = m_Socket.getOutputStream();
			if (m_Headers.containsKey("chunked") && m_Headers.get("chunked").equals("yes")) {
				m_Headers = Tools.SetupChunkedResponseHeaders(m_Type);
				String headersToReturn = createHeaders();
				returnChunked(outputStream, fileToReturn, headersToReturn);
			}
			else {
				m_Content = Tools.ReadFile(fileToReturn, m_Type);	
				m_Headers = Tools.SetupResponseHeaders(m_Content, m_Type);
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

	protected void returnChunked(OutputStream i_OutputStream, File i_FileToReturn, String i_HeadersToReturn) {
		FileInputStream fis = null;
		int chunkedSize = 30;
		try
		{
			i_OutputStream.write(i_HeadersToReturn.getBytes());
			i_OutputStream.flush();
			
			byte[] bFile = new byte[chunkedSize];
			fis = new FileInputStream(i_FileToReturn);
			while(fis.available() != 0)
			{
				fis.read(bFile, 0, bFile.length);
				try {
					i_OutputStream.write(bFile);
					i_OutputStream.flush();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IOException ioe) {
			try {
				new ErrorRequest(RequestFactory.m_InternalErrorPath, RequestFactory.m_InternalErrorHeader, m_Socket).ReturnResponse();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		finally {
			try {
				i_OutputStream.flush();
				i_OutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
