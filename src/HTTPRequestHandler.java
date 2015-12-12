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
			} catch (IOException e1) {
				e1.printStackTrace();
			};
			System.out.println(e);
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
		int buffSize = m_Socket.getReceiveBufferSize();
		int ret_read;
		byte[] buff = new byte[buffSize];
		StringBuilder requestHeaders = new StringBuilder();
		DataInputStream inputStream = new DataInputStream(m_Socket.getInputStream());
        while ((ret_read = inputStream.read(buff)) != -1) {
            requestHeaders.append(new String(buff, 0, ret_read));
        }
        
        requestHeaders.append(CRLF);
		System.out.println(requestHeaders);
		
		return requestHeaders.toString();
	}
}