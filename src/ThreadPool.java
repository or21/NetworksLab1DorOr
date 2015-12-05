import java.util.ArrayList;

public class ThreadPool {

	private ArrayList<Thread> m_RunningRequests;
	private ArrayList<Thread> m_WaitingRequests;
	private int m_MaxNumOfThreads;

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
		for (int i = 0; i < m_MaxNumOfThreads; i++) {
			if ((m_RunningRequests.get(i) != null) && (m_RunningRequests.get(i).getState() != Thread.State.TERMINATED)) { 
				m_RunningRequests.remove(i);
			}
		}
		
		while (m_RunningRequests.size() < m_MaxNumOfThreads) {
			Thread currentThread = m_WaitingRequests.get(0);
			m_WaitingRequests.remove(currentThread);
			m_RunningRequests.add(currentThread);
			System.out.println("Starts new thread");
			currentThread.start();
		}
	}
}
