import java.io.* ;
import java.net.* ;

public final class HTTPRequest implements Runnable
{
	public final static String CRLF = "\r\n";
		
	Socket m_Socket;
	Runnable m_Callback;

	// Constructor
	public HTTPRequest(Socket i_Socket, Runnable i_Callback)
	{
		this.m_Socket = i_Socket;
		this.m_Callback = i_Callback;
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
		ClientRequest clientRequest = RequestFactory.CreateRequest(request);
		clientRequest.ReturnResponse(m_Socket.getOutputStream());
		m_Socket.close();
		m_Callback.run();
	}

	private String readRequestFromClient() throws IOException {
		String inputMessage;
		StringBuilder requestHeaders = new StringBuilder();
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(m_Socket.getInputStream()));
		while((inputMessage = inputStream.readLine()) != null && inputMessage.length() > 0) {
			System.out.println(inputMessage);
			requestHeaders.append(inputMessage).append(CRLF);
		}
		System.out.println();
		requestHeaders.append(CRLF);
		return requestHeaders.toString();
	}
}