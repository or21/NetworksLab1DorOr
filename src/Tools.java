import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Tools {

	public static HashMap<String, String> SetupHeaders(byte[] i_Content, String i_Type) {
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Content-Type", i_Type);
		headers.put("Content-Length", String.valueOf(i_Content.length));
		return headers;
	}
	
	// TODO: Or. This doesn't work for images. Try to figure out why
	public static byte[] ReadFile(File i_File, String i_Type)
	{
		FileInputStream fis = null;
		try
		{
			byte[] bFile = new byte[(int)i_File.length()];
			fis = new FileInputStream(i_File);
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
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					
				}
			}
		}
	}
}
