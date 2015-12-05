import java.util.HashMap;

public class Tester {
	public static void main(String[] args) {
		ConfigFile config = ConfigFile.GetInstance();
		config.Parse("config.ini");
		HashMap<String, String> map = config.GetConfigurationParameters();
		System.out.println(map.get("port"));
	}
}
