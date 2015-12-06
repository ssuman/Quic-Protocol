package utils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestToBlocks {
	
	public Map<String, List<ByteBuffer>> requestToByteBuffer;	
	static Map<Integer, Boolean> ackedPack = new HashMap<>();
	
	public RequestToBlocks(Map<String, List<ByteBuffer>> req) {
		this.requestToByteBuffer = req;
	}
}
