public class Tester {
	public static void main(String[] args) {
		ConfigFile config = ConfigFile.GetInstance();
		config.Parse(ConfigFile.CONFIG_FILE_PATH);
		WebServer server = new WebServer(config);
		server.Run();
	}
}
