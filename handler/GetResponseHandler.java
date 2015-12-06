package handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import Segment.Segment;
import server.Connection;
import utils.RequestToBlocks;

public class GetResponseHandler implements Handler<Map<String, RequestToBlocks>>{

	String connectionId;
	Connection connection;
	List<ByteBuffer> buf;
	
	/**
	 * Constructor for Get Response Handler. 
	 * @param buf 
	 * @param connectionId		Connection ID for a client.
	 * @param con				Connection associated with a client.
	 */
	public GetResponseHandler(List<ByteBuffer> buf, String connectionId, Connection con) {
		this.connectionId = connectionId;
		this.connection = con;
		this.buf = buf;
	}

	/**
	 * TCP New Reno:
	 * If the SSTHRESH value is < 5000 call Slow Start Handler.
	 * If the SSTHRESH value is >= 5000 call Congestion Avoidance Handler.
	 * On Receiving 3 Dup Acks call the Fast Recovery Handler.
	 */
	@Override
	public void handle(Map<String, RequestToBlocks> connectionToResponse) throws IOException {
		RequestToBlocks listByteBuffer = connectionToResponse.get(connectionId);
		
		if(connection.CWND < connection.SSTHRESH){
			//TODO: Decide on the file format.
			new SlowStartHandler("",connection, buf).handle(listByteBuffer);
		}else{
			new CongestionAvoidanceHandler();
		}
		
	}


}
