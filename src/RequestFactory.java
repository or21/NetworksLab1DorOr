import java.util.Arrays;

public class RequestFactory {

	public static enum eMethods {
		GET,
		POST,
		HEAD,
		TRACE
	}

	public static ClientRequest CreateRequest(String i_Request) {
		String[] allHeaders = i_Request.split("\r");
		String[] firstHeader = allHeaders[0].split("[ ]+");
		if (firstHeader.length != 3) { 
			return new BadRequest();
		} else if (!checkValidPath(firstHeader[1])) {// TODO: Or. Check here if url (firstheader[1]) contains ".."
			return new ForbiddenRequest();
		} else {
			try {
				eMethods caseSwitch = eMethods.valueOf(firstHeader[0]);
				switch(caseSwitch) {
				case GET: 
					return new GetRequest(firstHeader, Arrays.copyOfRange(allHeaders, 1, allHeaders.length));		

				case POST: 
					return new PostRequest(firstHeader, Arrays.copyOfRange(allHeaders, 1, allHeaders.length));		

				case HEAD: 
					return new HeadRequest(firstHeader, Arrays.copyOfRange(allHeaders, 1, allHeaders.length));		

				case TRACE: 
					return new TraceRequest(firstHeader, Arrays.copyOfRange(allHeaders, 1, allHeaders.length), i_Request);
				
				default:
					return new NotImplementedRequest(); 
				}
			}
			catch (Exception e) {
				return new NotImplementedRequest();
			}
		}
	}

	private static boolean checkValidPath(String i_Url) {
		boolean isValid = true;
		
		if (!i_Url.startsWith("/")) {
			isValid = false;
		}
		
		if (i_Url.contains("/..")) {
			isValid = false;
		}
		
		return isValid;
	}
}
