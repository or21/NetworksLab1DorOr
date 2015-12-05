import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class GetRequest implements ClientRequest {

	private String m_Type;
	private String m_Url;
	private final String m_StaticFilesPath = "static/";
	private final String m_404NotFoundPath = "static/html/404NotFound.html";
	private HashMap<String, String> m_Headers;
	private String m_Content;

	public GetRequest(String[] firstHeader, String[] copyOfRange) {
		// TODO Auto-generated constructor stub
		m_Url = firstHeader[1];
		if (m_Url.equals("/")) {
			m_Type = "text/html";
		} else { // TODO: This doesn't care for parameters
			int i = firstHeader[1].lastIndexOf('.');
			if (i > 0) {
				String extension = firstHeader[1].substring(i + 1);
				m_Type = extension.equals("html") ? "text/html" :
					extension.equals("ico") ? "icon" :
						"image";
			}
		}
	}

	@Override
	public void ReturnResponse(OutputStream os) {
		// TODO Auto-generated method stub
		File toReturn;
		toReturn = (m_Url.equals("/") ? 
				new File(m_StaticFilesPath + "html/index.html") : 
					new File(m_StaticFilesPath + "html/" + m_Url));
		m_Content = new String(!toReturn.exists() ? 
				readFile(new File(m_404NotFoundPath)) : 
					readFile(toReturn));

		StringBuilder okString = new StringBuilder("HTTP/1.1 200 OK\r\n");
		m_Headers = setupHeaders(m_Content);

		for(String header : m_Headers.keySet()) {
			okString.append(header).append(": ").append(m_Headers.get(header)).append("\r\n");
		}
		okString.append("\r\n").append(m_Content);

		try {
			os.write(okString.toString().getBytes());
			os.flush();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private HashMap<String, String> setupHeaders(String i_Content) {
		HashMap<String, String> headers = new HashMap<>();
		headers.put("content-type", m_Type);
		headers.put("content-length", String.valueOf(i_Content.length()));
		return headers;
	}

	private byte[] readFile(File i_File)
	{
		try
		{
			FileInputStream fis = new FileInputStream(i_File);
			byte[] bFile = new byte[(int)i_File.length()];
			// read until the end of the stream.
			while(fis.available() != 0)
			{
				fis.read(bFile, 0, bFile.length);
			}
			return bFile;
		}
		catch(FileNotFoundException i_FNFE)
		{
			System.out.println("File not found");
			return null;
		}
		catch(IOException i_IOE)
		{
			System.out.println("IOException");
			return null;
		}
	}
}