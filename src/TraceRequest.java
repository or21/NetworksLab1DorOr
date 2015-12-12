import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class TraceRequest extends HeadRequest {
	
	private String m_request;

	public TraceRequest(String[] i_FirstHeaderRow, HashMap<String,String> requestHeaders, String i_Request, Socket i_Socket) {
		super (i_FirstHeaderRow, requestHeaders, i_Socket);

		m_request = i_Request;
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
			
			StringBuilder responseString = new StringBuilder(createHeaders());

			System.out.println(responseString);
			responseString.append("\r\n").append(m_Content.length).append("\r\n").append(m_request);

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
