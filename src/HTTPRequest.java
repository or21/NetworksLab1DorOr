import java.io.* ;
import java.net.* ;
import java.util.* ;

final class HTTPRequest implements Runnable
{
	final static String CRLF = "\r\n";
	Socket m_Socket;

	// Constructor
	public HTTPRequest(Socket i_Socket)
	{
		this.m_Socket = i_Socket;
	}

	// Implement the run() method of the Runnable interface.
	public void run()
	{
		try
		{
			processRequest();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception
	{
		String request = readRequestFromClient();
		String[] requestHeaders = request.split(CRLF);
		// 1. GET Request
		// 2. POST Request
		// 3. HEAD Request
		// 4. TRACE Request
		// 5. Unsupported
		
		DataOutputStream os = new DataOutputStream(m_Socket.getOutputStream());
		//Get the source IP
		String sourceIP = m_Socket.getInetAddress().getHostAddress();

		// Construct the response message.
		String statusLine = "HTTP/1.0 200 OK" + CRLF;
		String contentTypeLine = "Content-Type: text/html" + CRLF;
		String contentLength = "Content-Length: ";

		String entityBody = "<HTML>" + 
				"<HEAD><TITLE>"+ sourceIP +"</TITLE></HEAD>" +
				"<BODY><H1>"+ sourceIP +"</H1></BODY></HTML>";

		// Send the status line.
		os.writeBytes(statusLine);

		// Send the content type line.
		os.writeBytes(contentTypeLine);

		// Send content length.
		os.writeBytes(contentLength + entityBody.length() + CRLF);

		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);

		// Send the content of the HTTP.
		os.writeBytes(entityBody) ;

		// Close streams and socket.
		os.close();
		m_Socket.close();
	}

	private String readRequestFromClient() throws IOException {
		String inputMessage;
		StringBuilder requestHeaders = new StringBuilder();
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(m_Socket.getInputStream()));
		
		while((inputMessage = inputStream.readLine()) != null && inputMessage.length() > 0) {
			System.out.println(inputMessage);
			requestHeaders.append(inputMessage);
		}
		System.out.println();
		
		return requestHeaders.toString();
	}

}