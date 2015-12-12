import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class BadRequest implements IClientRequest {

	private final String m_Type = "text/html";
	private final String m_BadRequestPath = "static/html/400BadRequest.html";
	private final String m_Header = "HTTP/1.1 400 Bad Request\r\n";
	private Socket m_Socket;
	
	public BadRequest(Socket i_Socket) {
		this.m_Socket = i_Socket;
	}

	@Override
	public void ReturnResponse() throws IOException {
		OutputStream outputStream = m_Socket.getOutputStream();
		StringBuilder responseString = new StringBuilder();
		responseString.append(m_Header);
		
		byte[] content = Tools.ReadFile(new File(m_BadRequestPath), m_Type);
		HashMap<String, String> defaultHeaders = Tools.SetupResponseHeaders(content, m_Type);
		for(String header : defaultHeaders.keySet()) {
			responseString.append(header).append(": ").append(defaultHeaders.get(header)).append("\r\n");
		}
		
		System.out.println(responseString);
		responseString.append("\r\n");
		
		try {
			outputStream.write(responseString.toString().getBytes());
			outputStream.write(content);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
