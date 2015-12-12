import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class TraceRequest extends HeadRequest {
	
	private String m_Request;

	public TraceRequest(String[] i_FirstHeaderRow, HashMap<String,String> requestHeaders, String i_Request, Socket i_Socket) {
		super (i_FirstHeaderRow, requestHeaders, i_Socket);
		m_Request = i_Request;
	}

	@Override
	public void ReturnResponse() throws IOException {
		OutputStream outputStream = m_Socket.getOutputStream();
		File fileToReturn;
		fileToReturn = openFileAccordingToUrl(m_Url);
		if (!fileToReturn.exists()) {
			ReturnNotFoundResponse();
		} else {
			m_Content = m_Request.getBytes();
			m_Headers = Tools.SetupResponseHeaders(m_Content, m_Type);
			StringBuilder responseString = new StringBuilder(createHeaders());
			responseString.append("\r\n").append(m_Request.length())
			.append("\r\n").append(m_Request);
			System.out.println(responseString);

			try {
				outputStream.write(responseString.toString().getBytes());
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
