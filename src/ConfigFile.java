import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Singleton implementation of the config file
 * 
 * @author Dor Or
 *
 */
public class ConfigFile implements IParser {
	
	public static final String CONFIG_FILE_PATH = "config/config.ini";
	public static final String CONFIG_FILE_ROOT_KEY = "root";
	public static final String CONFIG_FILE_DEFAULT_PAGE_KEY = "defaultPage";
	
	private HashMap<String, String> m_ConfigDictionary;

	private static ConfigFile m_Instance = null;
	private static Object m_Lock = new Object();

	private ConfigFile() {}

	public static ConfigFile GetInstance() {
		if (m_Instance == null) {
			synchronized (m_Lock) {
				if (m_Instance == null) {
					m_Instance = new ConfigFile();					
				}
			}
		}
		return m_Instance;
	}
	
	@Override
	public void Parse(String i_Filename) {
		m_ConfigDictionary = new HashMap<>();
		byte[] contentAsByteArray = readFile(new File(i_Filename));
		String[] content = new String(contentAsByteArray).split("\r\n");
		for(String line : content) {
			String[] keyValuePair = line.split("=");
			m_ConfigDictionary.put(keyValuePair[0], keyValuePair[1]);
		}
	}

	private byte[] readFile(File i_File)
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(i_File);
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
		finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public HashMap<String, String> GetConfigurationParameters() {
		return m_ConfigDictionary;
	}
}
