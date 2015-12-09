public class PostRequest extends GetRequest {

	public PostRequest(String[] i_FirstHeaderRow, String[] i_AllOtherHeaders) {
		super(i_FirstHeaderRow, i_AllOtherHeaders);
		
		if (!i_AllOtherHeaders[i_AllOtherHeaders.length - 1].equals("")) {
			parseParams(i_AllOtherHeaders[i_AllOtherHeaders.length - 1]);
		}
	}
}
