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
	}
	
	public void Manage() {
		
	}
}
