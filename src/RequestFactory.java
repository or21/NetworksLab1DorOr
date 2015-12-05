import java.util.Arrays;

public class RequestFactory {
	final static String GET_METHOD = "GET";
	final static String POST_METHOD = "POST";
	final static String HEAD_METHOD = "HEAD";
	final static String TRACE_METHOD = "TRACE";

	public static ClientRequest CreateRequest(String i_RequestInput) {
		String[] allHeaders = i_RequestInput.split(HTTPRequest.CRLF);
		String[] firstHeader = allHeaders[0].split("[ ]+");
		if (firstHeader.length != 3) { // TODO: Or. Check here if url (firstheader[1]) contains ".."
			return new BadRequest();
		} else {
			switch(firstHeader[0]) {
				case GET_METHOD: {
					return new GetRequest(firstHeader, Arrays.copyOfRange(allHeaders, 1, allHeaders.length));		
				}
				case POST_METHOD: {
					return new PostRequest(firstHeader, Arrays.copyOfRange(allHeaders, 1, allHeaders.length));		
				}
				case HEAD_METHOD: {
					return new HeadRequest(firstHeader, Arrays.copyOfRange(allHeaders, 1, allHeaders.length));		
				}
				case TRACE_METHOD: {
					return new TraceRequest(firstHeader, Arrays.copyOfRange(allHeaders, 1, allHeaders.length));
				}
				default: {
					return null;
				}
			}
		}
	}
}
