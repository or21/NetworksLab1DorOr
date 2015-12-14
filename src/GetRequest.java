import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class GetRequest extends HeadRequest {

	protected HashMap<String, String> m_Params;
	byte[] CRLFinByteArray = CRLF.getBytes();

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
			responseString.append(CRLF);
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
	private void writeChunked(DataOutputStream i_OutputStream, byte[] i_HeadersData) throws NumberFormatException, IOException {
		int chunkSize = 1024;
		int bytesCounter = 0;
		int amountOfData = m_Content.length;
		int leftToWrite = amountOfData - bytesCounter;
		byte[] bytesToWriteArray;
		
		byte[] dataToSend = new byte[amountOfData];
		//System.arraycopy(i_HeadersData, 0, dataToSend, 0, i_HeadersData.length);
		System.arraycopy(m_Content, 0, dataToSend, 0, m_Content.length);

		System.out.println(new String(i_HeadersData));
		i_OutputStream.write(i_HeadersData);
		
		// Build each chunk according to chunkSize
		while (leftToWrite > 0) {
			if (leftToWrite >= chunkSize - CRLFinByteArray.length) {
				bytesToWriteArray = new byte[chunkSize];
				System.arraycopy(dataToSend, bytesCounter, bytesToWriteArray, 0, chunkSize - CRLFinByteArray.length);
				bytesCounter += (chunkSize - CRLFinByteArray.length);
				System.arraycopy(CRLFinByteArray, 0, bytesToWriteArray, chunkSize - CRLFinByteArray.length, CRLFinByteArray.length);
			} else {
				bytesToWriteArray = new byte[leftToWrite + CRLFinByteArray.length];
				System.arraycopy(dataToSend, bytesCounter, bytesToWriteArray, 0, leftToWrite);
				bytesCounter += leftToWrite;
				System.arraycopy(CRLFinByteArray, 0, bytesToWriteArray, leftToWrite, CRLFinByteArray.length);
			}
			
			sendChunk(i_OutputStream, bytesToWriteArray);
			leftToWrite = amountOfData - bytesCounter;
		}
		
		i_OutputStream.write((Integer.toHexString(0)).getBytes());
		i_OutputStream.write(CRLFinByteArray);
		i_OutputStream.write(CRLFinByteArray);
	}

	private void sendChunk(DataOutputStream i_OutputStream, byte[] i_BytesToWriteArray) throws IOException {
		System.out.println(i_BytesToWriteArray.length + CRLF + new String(i_BytesToWriteArray));
		i_OutputStream.write((Integer.toHexString(i_BytesToWriteArray.length) + CRLF).getBytes());
		i_OutputStream.write(i_BytesToWriteArray);
		i_OutputStream.flush();
	}
}