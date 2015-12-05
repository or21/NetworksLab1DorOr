import java.util.HashMap;

public class Tester {
	public static void main(String[] args) {
		ConfigFile config = ConfigFile.GetInstance();
		config.Parse(ConfigFile.CONFIG_FILE_PATH);
		HashMap<String, String> map = config.GetConfigurationParameters();
		System.out.println(map);
		
		WebServer server = new WebServer(config);
		server.Run();
	}
}
