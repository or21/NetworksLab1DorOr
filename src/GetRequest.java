import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class GetRequest extends HeadRequest {

	protected HashMap<String, String> m_Params;

	public GetRequest(String[] i_FirstHeaderRow, HashMap<String, String> requestHeaders, Socket i_Socket) {
		super(i_FirstHeaderRow, requestHeaders, i_Socket);

		if (m_Url.contains("?")) {
			m_Params = new HashMap<String, String>();
			parseParams(m_Url.substring(m_Url.indexOf("?") + 1));
		}
	}

	protected void parseParams(String i_ParamsString) {
		String[] parametersArray = i_ParamsString.split("&");

		for (String string : parametersArray) {
			String[] keyValuePair = string.split("=");
			if (keyValuePair.length == 1) {
				m_Params.put(keyValuePair[0], "");
			} else {
				m_Params.put(keyValuePair[0], keyValuePair[1]);
			}
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
			m_Headers = Tools.SetupResponseHeaders(m_Content, m_Type);
			System.out.println(fileToReturn.getAbsolutePath());
			StringBuilder responseString = new StringBuilder(createHeaders());

			System.out.println(responseString);

			responseString.append(CRLF);

			try {
				outputStream.write(responseString.toString().getBytes());
				outputStream.write(m_Content);
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}