import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class ForbiddenRequest implements ClientRequest {
	
	private final String m_Type = "text/html";
	private final String m_ForbiddenRequestPath = "static/html/400ForbiddenRequest.html";
	private final String m_Header = "HTTP/1.1 400 Forbidden\r\n";

	@Override
	public void ReturnResponse(OutputStream i_OutputStream) {
		StringBuilder responseString = new StringBuilder();
		responseString.append(m_Header);
		
		String content = new String(Tools.ReadFile(new File(m_ForbiddenRequestPath), m_Type));
		HashMap<String, String> defaultHeaders = Tools.SetupHeaders(content, m_Type);
		for(String header : defaultHeaders.keySet()) {
			responseString.append(header).append(": ").append(defaultHeaders.get(header)).append("\r\n");
		}
		
		System.out.println(responseString);
		responseString.append("\r\n").append(content);
		
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
