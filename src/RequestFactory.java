import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class RequestFactory {

	public static IClientRequest CreateRequest(String i_Request, Socket i_Socket) {
		String[] requestSplitByBreak = i_Request.split("\r\n\r\n");
		String[] allHeaders = requestSplitByBreak[0].split("\r\n");
		HashMap<String, String> requestHeaders = Tools.SetupRequestHeaders(Arrays.copyOfRange(allHeaders, 1, allHeaders.length));
		
		if (requestSplitByBreak[1] != null) {
			requestHeaders.put("params", requestSplitByBreak[1]);
		}
		
		String[] firstHeader = allHeaders[0].split("[ ]+");
		if ((firstHeader.length != 3) || (!eSupportedHTTP.isInEnum(firstHeader[2]))){ 
			return new BadRequest(i_Socket);
		} else if (!checkValidPath(firstHeader[1])) {
			return new ForbiddenRequest(i_Socket);
		} else {
			try {
				eMethods caseSwitch = eMethods.valueOf(firstHeader[0]);
				switch(caseSwitch) {
				case GET: 
					return new GetRequest(firstHeader, requestHeaders, i_Socket);		

				case POST: 
					return new PostRequest(firstHeader, requestHeaders, i_Socket);		

				case HEAD: 
					return new HeadRequest(firstHeader, requestHeaders, i_Socket);		

				case TRACE: 
					return new TraceRequest(firstHeader, requestHeaders, i_Request, i_Socket);
				
				default:
					return new NotImplementedRequest(i_Socket); 
				}
			}
			catch (IllegalArgumentException iae) {
				return new NotImplementedRequest(i_Socket);
			}
			catch (NullPointerException npe) {
				return new NotImplementedRequest(i_Socket);
			}
			catch (Exception e) {
				return new InternalServerError(i_Socket);
			}
		}
	}

	private static boolean checkValidPath(String i_Url) {
		boolean isValid = true;
		
		if (!i_Url.startsWith("/")) {
			isValid = false;
		}
		
		return isValid;
	}
	
	public static enum eMethods {
		GET,
		POST,
		HEAD,
		TRACE
	}
	
	public enum eSupportedHTTP {
		ONE("HTTP/1.0"),
		ONEPOINTONE("HTTP/1.1");
		
		private final String m_Value;
		
		eSupportedHTTP (String value) { 
			this.m_Value = value; 
		}
		
	    public String getValue() { 
	    	return m_Value; 
    	}
	    
	    public static boolean isInEnum(String str) {
	    	boolean isEnumValue = false;
	    	try {
	    		for (eSupportedHTTP enumVar : eSupportedHTTP.values()) {
					if (str.equals(enumVar.getValue())) {
						isEnumValue = true;
					}
				}
	    	}
	    	catch (Exception e) {
	    	}
	    	
	    	return isEnumValue;
	    }
	}
}
