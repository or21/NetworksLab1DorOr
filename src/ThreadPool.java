import java.util.ArrayList;

public class ThreadPool {

	private ArrayList<Thread> m_RunningRequests;
	private ArrayList<Thread> m_WaitingRequests;
	private int m_MaxNumOfThreads;
	private Object m_ManageLock = new Object();

	public ThreadPool(int i_MaxNumOfThreads) {
		this.m_MaxNumOfThreads = i_MaxNumOfThreads;
		this.m_RunningRequests = new ArrayList<Thread>(m_MaxNumOfThreads);
		this.m_WaitingRequests = new ArrayList<Thread>();
	}

	public void AddThread(Thread i_ThreadToAdd) {
		m_WaitingRequests.add(i_ThreadToAdd);
		Manage();

		// Thread finishes, calls Manage()
	}

	public void Manage() {
		synchronized (m_ManageLock) {
			for (int i = 0; i < m_RunningRequests.size(); i++) {
				if ((m_RunningRequests.get(i) != null) && (m_RunningRequests.get(i).getState() == Thread.State.TERMINATED)) { 
					m_RunningRequests.remove(i);
				}
			}

			if ((m_RunningRequests.size() < m_MaxNumOfThreads) && (m_WaitingRequests.size() > 0)) {
				Thread currentThread = m_WaitingRequests.get(0);
				m_WaitingRequests.remove(0);
				m_RunningRequests.add(currentThread);
				System.out.println("Starts new thread: " + m_RunningRequests.size() + "\n");
				currentThread.start();
			}
		}
	}
}
