import java.io.DataOutputStream;
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

	/*
	 * Parse the parameters from the request
	 */
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

	/*
	 * Build the response for GET request
	 */
	@Override
	public void ReturnResponse() throws IOException {
		OutputStream outputStream = m_Socket.getOutputStream();
		File fileToReturn;
		fileToReturn = openFileAccordingToUrl(m_Url);
		if (!fileToReturn.exists()) {
			ReturnNotFoundResponse();
		} 

		// Create chunk response
		if (m_ShouldSendChunked) {

			m_Headers = Tools.SetupChunkedResponseHeaders(m_Type);
			String headersToReturn = createHeaders();
			m_Content = Tools.ReadFile(fileToReturn);
			StringBuilder responseString = new StringBuilder(headersToReturn);
			responseString.append(CRLF).append(m_Content);
			writeChunked(new DataOutputStream(outputStream), responseString.toString().getBytes());
		} else {
			// Create and send regular response
			m_Content = Tools.ReadFile(fileToReturn);
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

	/*
	 * Write the response in chunks
	 */
	private void writeChunked(DataOutputStream outputStream,
			byte[] responseData) throws NumberFormatException, IOException {
		int chunkSize = 1024;
		int bytesCounter = 0;
		byte[] bytesToWriteArray;
		int leftToWrite = responseData.length - bytesCounter;

		// Build each chunk according to chunkSize
		while (responseData.length - bytesCounter > 0) {
			if (responseData.length - bytesCounter > chunkSize) {
				bytesToWriteArray = new byte[chunkSize];
				System.arraycopy(responseData, bytesCounter, bytesToWriteArray, 0, chunkSize);
				bytesCounter += chunkSize;
			} else {
				bytesToWriteArray = new byte[leftToWrite];
				System.arraycopy(responseData, bytesCounter, bytesToWriteArray, 0, leftToWrite);
				bytesCounter += leftToWrite;
			}

			sendChunk(outputStream, bytesToWriteArray);
			leftToWrite = responseData.length - bytesCounter;
		}
		
		outputStream.write((Integer.toHexString(0)).getBytes());
		outputStream.write(CRLF.getBytes());
	}

	private void sendChunk(DataOutputStream outputStream, byte[] bytesToWriteArray) throws IOException {
		System.out.println(new String(bytesToWriteArray));
		outputStream.write((Integer.toHexString(bytesToWriteArray.length) + CRLF).getBytes());
		outputStream.write(bytesToWriteArray);
		outputStream.write(CRLF.getBytes());
	}
}