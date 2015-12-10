import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class WebServer {

	private int m_Port, m_MaxThreads;
	private ThreadPool m_ThreadPool;
	private ServerSocket m_ServerSocket;

	/**
	 * Constructor, receives a configuration files, and parses its keys
	 * @param i_ConfigFile
	 */
	public WebServer(ConfigFile i_ConfigFile) {
		HashMap<String, String> configParams = i_ConfigFile.GetConfigurationParameters();
		m_Port = Integer.parseInt(configParams.get("port"));
		m_MaxThreads = Integer.valueOf(configParams.get("maxThreads"));
		m_ThreadPool = new ThreadPool(m_MaxThreads);
		m_ServerSocket = createServerSocket();
	}

	public void Run() {
		System.out.println("Listening on port: " + m_Port);
		while (true)
		{
			Socket connection = waitForConnection();
			if (connection == null) { 
				continue;
			} else {
				System.out.println("Recieved a new HTTP request");
				HTTPRequestHandler request = new HTTPRequestHandler(connection, new Runnable() {

					@Override
					public void run() {
						onFinishThread();
					}
				});

				Thread thread = new Thread(request);
				m_ThreadPool.AddThread(thread);
			}
		}
	}

	private Socket waitForConnection() {
		try {
			return m_ServerSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ServerSocket createServerSocket() {
		try {
			return new ServerSocket(m_Port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void onFinishThread() {
		System.out.println("Thread Finished");
		m_ThreadPool.Manage();
	}
}
