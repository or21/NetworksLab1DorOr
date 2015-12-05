import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class WebServer {

	private int m_Port, m_MaxThreads;
	private String m_Root, m_DefaultPage;
	private ThreadPool m_ThreadPool;
	private ServerSocket m_ServerSocket;

	/**
	 * Constructor, receives a configuration files, and parses its keys
	 * @param i_ConfigFile
	 */
	public WebServer(ConfigFile i_ConfigFile) {
		HashMap<String, String> configParams = i_ConfigFile.GetConfigurationParameters();
		m_Port = Integer.parseInt(configParams.get("port"));
		m_DefaultPage = configParams.get("defaultPage");
		m_Root = configParams.get("root");
		m_MaxThreads = Integer.valueOf(configParams.get("maxThreads"));
		m_ThreadPool = new ThreadPool(m_MaxThreads);
		m_ServerSocket = createServerSocket(m_Port);
	}
	
	public void Run() {
		while (true)
		{
			Socket connection = waitForConnection();
			if (connection == null) { 
				continue;
			} else {
				System.out.println("Recieved a new HTTP request");
				HTTPRequest request = new HTTPRequest(connection);
				Thread thread = new Thread(request);
				thread.start();
			}
		}
	}

	private Socket waitForConnection() {
		try {
			return m_ServerSocket.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private ServerSocket createServerSocket(int m_Port2) {
		try {
			return new ServerSocket(m_Port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
