import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class TraceRequest extends HeadRequest {
	
	private String m_request;

	public TraceRequest(String[] i_FirstHeaderRow, String[] i_AllOtherHeaders, String i_Request) {
		super (i_FirstHeaderRow, i_AllOtherHeaders);

		m_request = i_Request;
	}

	@Override
	public void ReturnResponse(OutputStream i_OutputStream) {
		// TODO Auto-generated method stub
		File fileToReturn;
		fileToReturn = (m_Url.equals(ConfigFile.GetInstance().GetConfigurationParameters().get("root")) ? 
				new File(m_StaticFilesPath + "html/" + ConfigFile.GetInstance().GetConfigurationParameters().get("defaultPage")) : 
					new File(m_StaticFilesPath + "html/" + m_Url));
		if (!fileToReturn.exists()) {
			new NotFoundRequest().ReturnResponse(i_OutputStream);
		} else {
			m_Content = new String(Tools.ReadFile(fileToReturn, m_Type));
			
			StringBuilder responseString = new StringBuilder(createHeaders());

			System.out.println(responseString);
			responseString.append("\r\n").append(m_Content.length()).append("\r\n").append(m_request);

			try {
				i_OutputStream.write(responseString.toString().getBytes());
				i_OutputStream.flush();
				i_OutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
