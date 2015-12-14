import java.io.* ;
import java.net.* ;

public final class HTTPRequestHandler extends Thread
{
	public final static String CRLF = "\r\n";
		
	private Socket m_Socket;
	private Runnable m_Callback;
	
	// Constructor
	public HTTPRequestHandler(Socket i_Socket, Runnable i_Callback)
	{
		this.m_Socket = i_Socket;
		this.m_Callback = i_Callback;
	}

	// Implement the handler routine.
	public void run()
	{
		try
		{
			processRequest();
		}
		catch (Exception e)
		{
			if (m_Socket != null && m_Socket.isClosed()) {
					try {
						new ErrorRequest(RequestFactory.m_InternalErrorPath, RequestFactory.m_InternalErrorHeader, m_Socket).ReturnResponse();
					} catch (IOException ioe) {
						System.out.println(ioe.getMessage());
					}					
			} else {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Create request according to client and return callback when connection is closed
	 * @throws Exception
	 */
	private void processRequest() throws Exception
	{
		String request = readRequestFromClient();
		IClientRequest clientRequest = RequestFactory.CreateRequest(request, m_Socket);
		clientRequest.ReturnResponse();
		m_Socket.close();
		m_Callback.run();
	}

	/*
	 * Read the request from the client
	 */
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
        System.out.println();
		return requestHeaders.toString();
	}
	
	public Socket getSocket() {
		return m_Socket;
	}
}