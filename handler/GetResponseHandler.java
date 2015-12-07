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
	List<Segment> buf;
	String path;
	/**
	 * Constructor for Get Response Handler. 
	 * @param buf 
	 * @param connectionId		Connection ID for a client.
	 * @param con				Connection associated with a client.
	 * @param path 
	 */
	public GetResponseHandler(List<Segment> buf, String connectionId, Connection con, String path) {
		this.connectionId = connectionId;
		this.connection = con;
		this.buf = buf;
		this.path = path;
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
		
		if(connection.SND_CWND < connection.SSTHRESH){
			//TODO: Decide on the file format.
			new SlowStartHandler(path,connection, buf).handle(listByteBuffer);
		}else{
			new CongestionAvoidanceHandler();
		}
		
	}


}
