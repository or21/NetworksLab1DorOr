import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class WebServer {

	private ConfigFile m_config;

	int port = 8080;

	public void Run() {
		// Establish the listen socket.
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Process HTTP service requests in an infinite loop.
		while (true)
		{
			// Listen for a TCP connection request.
			Socket connection = null;
			try {
				connection = socket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Construct an object to process the HTTP request message.
			HTTPRequest request = new HTTPRequest(connection);

			// Create a new thread to process the request.
			Thread thread = new Thread(request);

			// Start the thread.
			thread.start();
		}
	}
}
