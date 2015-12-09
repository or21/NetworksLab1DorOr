import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class GetRequest implements ClientRequest {

	private String m_Type;
	private String m_Url;
	private final String m_StaticFilesPath = "static/";
	private final String m_404NotFoundPath = "static/html/404notfound.html";
	private HashMap<String, String> m_Headers;
	private String m_Content;

	public GetRequest(String[] i_FirstHeaderRow, String[] i_AllOtherHeaders) {
		m_Url = i_FirstHeaderRow[1];
		if (m_Url.equals("/")) {
			m_Type = "text/html";
		} else { // TODO: Dor. This doesn't care for parameters
			int i = i_FirstHeaderRow[1].lastIndexOf('.');
			if (i > 0) {
				String extension = i_FirstHeaderRow[1].substring(i + 1);
				m_Type = extension.equals("html") ? "text/html" :
					extension.equals("ico") ? "icon" : // TODO: Dor. Place an icon, so that you can send it back, and not get a file not found...
						"image";
			}
		}
	}

	@Override
	public void ReturnResponse(OutputStream i_OutputStream) {
		// TODO Auto-generated method stub
		File toReturn;
		toReturn = (m_Url.equals("/") ? 
				new File(m_StaticFilesPath + "html/index.html") : 
					new File(m_StaticFilesPath + "html/" + m_Url));
		if (!toReturn.exists()) {
			m_Type = "text/html";
			m_Content = new String(Tools.ReadFile(new File(m_404NotFoundPath), m_Type));
		} else {
			m_Content = new String(Tools.ReadFile(toReturn, m_Type));			
		}
		
		StringBuilder responseString = new StringBuilder("HTTP/1.1 200 OK\r\n");
		m_Headers = Tools.SetupHeaders(m_Content, m_Type);

		for(String header : m_Headers.keySet()) {
			responseString.append(header).append(": ").append(m_Headers.get(header)).append("\r\n");
		}
		
		System.out.println(responseString);
		responseString.append("\r\n").append(m_Content);
		
		try {
			i_OutputStream.write(responseString.toString().getBytes());
			i_OutputStream.flush();
			i_OutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}