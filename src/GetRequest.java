import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class GetRequest implements ClientRequest {

	private String m_Type;
	private String m_Url;
	private final String m_StaticFilesPath = "static/";
	private final String m_404NotFoundPath = "static/html/404NotFound.html";
	private HashMap<String, String> m_Headers;
	private String m_Content;

	public GetRequest(String[] i_FirstHeaderRow, String[] i_AllOtherHeaders) {
		m_Url = i_FirstHeaderRow[1];
		if (m_Url.equals("/")) {
			m_Type = "text/html";
		} else { // TODO: Dor. This doesn't care for parameters
			int i = i_FirstHeaderRow[1].lastIndexOf('.');
			if (i > 0) {
				String extension = i_FirstHeaderRow[1].substring(i + 1);
				m_Type = extension.equals("html") ? "text/html" :
					extension.equals("ico") ? "icon" : // TODO: Dor. Place an icon, so that you can send it back, and not get a file not found...
						"image";
			}
		}
	}

	@Override
	public void ReturnResponse(OutputStream i_OutputStream) {
		// TODO Auto-generated method stub
		File toReturn;
		toReturn = (m_Url.equals("/") ? 
				new File(m_StaticFilesPath + "html/index.html") : 
					new File(m_StaticFilesPath + "html/" + m_Url));
		m_Content = new String(!toReturn.exists() ? 
				readFile(new File(m_404NotFoundPath)) : 
					readFile(toReturn));

		StringBuilder responseString = new StringBuilder("HTTP/1.1 200 OK\r\n");
		m_Headers = setupHeaders(m_Content);

		for(String header : m_Headers.keySet()) {
			responseString.append(header).append(": ").append(m_Headers.get(header)).append("\r\n");
		}
		responseString.append("\r\n").append(m_Content);

		try {
			i_OutputStream.write(responseString.toString().getBytes());
			i_OutputStream.flush();
			i_OutputStream.close();
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

	// TODO: Or. This doesn't work for images. Try to figure out why
	private byte[] readFile(File i_File)
	{
		try
		{
			byte[] bFile = new byte[(int)i_File.length()];
			if (m_Type.equals("text/html") || m_Type.equals("icon")) {

				FileInputStream fis = new FileInputStream(i_File);
				while(fis.available() != 0)
				{
					fis.read(bFile, 0, bFile.length);
				}
			} else {
				BufferedImage image = ImageIO.read(i_File);
				WritableRaster raster = image.getRaster();
				DataBufferByte data = (DataBufferByte) raster.getDataBuffer(); 
				bFile = data.getData();
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