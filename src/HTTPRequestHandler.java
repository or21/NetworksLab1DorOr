import java.io.* ;
import java.net.* ;

public final class HTTPRequestHandler implements Runnable
{
	public final static String CRLF = "\r\n";
		
	Socket m_Socket;
	Runnable m_Callback;
	
	// Constructor
	public HTTPRequestHandler(Socket i_Socket, Runnable i_Callback)
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
		catch (SocketException se) {
			System.out.println(se.getMessage());
		}
		catch (IllegalArgumentException iae) {
			try {
				new BadRequest(m_Socket).ReturnResponse();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			try {
				new InternalServerError(m_Socket).ReturnResponse();
			} catch (SocketException se) {
				System.out.println(se.getMessage());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private void processRequest() throws Exception
	{
		String request = readRequestFromClient();
		IClientRequest clientRequest = RequestFactory.CreateRequest(request, m_Socket);
		clientRequest.ReturnResponse();
		m_Socket.close();
		m_Callback.run();
	}

	private String readRequestFromClient() throws IOException {
		boolean postFlag = false;
		String ret_read;
		StringBuilder requestHeaders = new StringBuilder();
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(m_Socket.getInputStream()));
        while (!(ret_read = inputStream.readLine()).equals("")) {
        	if (ret_read.startsWith("POST")) {
        		postFlag = true;
        	}
        	requestHeaders.append(ret_read);
        	requestHeaders.append(CRLF);
        }
        if (postFlag) {
        	requestHeaders.append(CRLF);
        	while(inputStream.ready()) {
        		requestHeaders.append((char) inputStream.read());
        	}
        }
                
        System.out.println(requestHeaders);
		return requestHeaders.toString();
	}
}