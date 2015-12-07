package utils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Segment.Segment;

public class RequestToBlocks {
	
	public Map<String, List<ByteBuffer>> requestToByteBuffer;
	
	// sequence Number  -> count of number of acks
	public Map<Integer, Integer> ackedPack = new HashMap<>();
	
	// sequence Number -> count 
	public Map<Integer, Integer> unAckedPack = new HashMap<>();
	
	
	public RequestToBlocks(Map<String, List<ByteBuffer>> req) {
		this.requestToByteBuffer = req;
	}
	
	
	public ByteBuffer getUnAckedPacket(String name,int seqNbr){
		List<ByteBuffer> buf = requestToByteBuffer.get(name);
		return buf.get(seqNbr);
	}

	public  boolean isDupACK(Segment segment) {
		int count = 0;
		if(ackedPack.containsKey(segment.acknowledgementNbr)){
			count = ackedPack.get(segment.acknowledgementNbr);
			ackedPack.put(segment.acknowledgementNbr, ++count);
		}else{
			ackedPack.put(segment.acknowledgementNbr, 0);
		}
		if(count >= 3){
			return true;
		}
		else{
			return false;
		}
	}
}
