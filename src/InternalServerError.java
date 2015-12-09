import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class InternalServerError implements ClientRequest {
	
	private final String m_Type = "text/html";
	private final String m_InternalErrorPath = "static/html/500InternalError.html";
	private final String m_Header = "HTTP/1.1 500 Internal Server Error\r\n";

	@Override
	public void ReturnResponse(OutputStream i_OutputStream) {
		StringBuilder responseString = new StringBuilder();
		responseString.append(m_Header);
		
		byte[] content = Tools.ReadFile(new File(m_InternalErrorPath), m_Type);
		HashMap<String, String> defaultHeaders = Tools.SetupHeaders(content, m_Type);
		for(String header : defaultHeaders.keySet()) {
			responseString.append(header).append(": ").append(defaultHeaders.get(header)).append("\r\n");
		}
		
		System.out.println(responseString);
		responseString.append("\r\n");
		
		try {
			i_OutputStream.write(responseString.toString().getBytes());
			i_OutputStream.write(content);
			i_OutputStream.flush();
			i_OutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
