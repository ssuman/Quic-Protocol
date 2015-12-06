package handler;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import Segment.Segment;
import server.Connection;
import server.Flags;
import utils.RequestToBlocks;

public class ServerWriterHandler implements Handler<SelectionKey> {

	// Channel to a Queue ByteBuffer.
	private final Map<DatagramChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
	
	// Connection ID to a Connection.
	final static Map<String , Connection> connections = new HashMap<>();
	
	// Connection ID to a list of byte buffers of a file
	final static Map<String, RequestToBlocks> connectionToResponse = new HashMap<>();
	
	public ServerWriterHandler(){
	}

	
	/**
	 * If the request received is a CHLO - call onCHLO()
	 * If the request received is a REQ  - call onREQ()
	 * If the request received is a ACK  - call onACK()
	 */
	@Override
	public void handle(SelectionKey key) throws IOException {
		
		DatagramChannel channel = (DatagramChannel) key.channel();
		Queue<ByteBuffer> queue = pendingData.get(channel);
		SocketAddress address = (SocketAddress) key.attachment();
		ByteBuffer buffer = queue.poll();
		
		Segment segment = null;
		try {
			 segment = Segment.deserializeBytes(buffer);
			
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		
		switch(segment.flag){
		case CHLO:
			onCHLO(channel, segment, address);
			break;
		case REQ:
			onREQ(channel, segment, address);
			break;
		case ACK:
			onACK(channel, segment, address);
			break;
		default:
			break;
			
		}
		key.interestOps(SelectionKey.OP_READ);
	}

	/**
	 * First time a request a received with CHLO flag. The following function is called.
	 * The server responds to it by sending a SHLO flag with data as null and ACK being the
	 * largest observed sequence number. That is the CHLO sequence number 0.
	 * 
	 * @param channel
	 * @param segment
	 * @param address
	 * @throws IOException
	 */
	private void onCHLO(DatagramChannel channel, Segment segment, SocketAddress address) throws IOException {
		Connection con = new Connection(address, 1000);
		connections.put(segment.connectionId, con);
		// create a new segment with SHLO flag, data null and ackNbr = +1
		// segment acknowledge number 
		Segment seg = new Segment();
		seg.flag = Flags.SHLO;
		seg.data = null;
		seg.acknowledgementNbr = segment.sequenceNbr;
		sendData(seg, address, channel);
	}


	/***
	 * On receiving a request. Get the connection and update the ip address to provide 
	 * client roaming. 
	 * 
	 * Create a new instance of get request handler which takes the path from GET request
	 * And reads the file and puts the data in the a map.
	 * 
	 * TODO:
	 * Also calls get response handler and send the parts of the data to client. based on the 
	 * congestion control algorithms
	 * 
	 * Finally send the packet.
	 * 
	 * @param channel
	 * @param segment
	 * @param address
	 * @throws IOException 
	 */
	private void onREQ(DatagramChannel channel, Segment segment, SocketAddress address) throws IOException {
		Connection conn = connections.get(segment.connectionId);
		conn.address = address;
		new GetRequestHandler(connectionToResponse).handle(segment);
		List<ByteBuffer> buf = new ArrayList<>();
		new GetResponseHandler(buf,segment.connectionId, conn).handle(connectionToResponse);
		for(ByteBuffer bf : buf){
			Segment seg = new Segment();
			seg.acknowledgementNbr = segment.sequenceNbr;
			//TODO: configure segment.
			
			sendData(seg, address, channel);
		}
		
	}


	private void onACK(DatagramChannel channel, Segment segment, SocketAddress address) {
		
		
	}

	/**
	 * Send the data to the client.
	 * 
	 * @param seg					New segment
	 * @param address				Socket Address of the client
	 * @param channel				Channel of the communication to the client
	 * @throws IOException			Throws IOException while trying to write to network.
	 */
	public void sendData(Segment seg, SocketAddress address, DatagramChannel channel) throws IOException{
		byte [] packBytes = Segment.serializeToBytes(seg);
		ByteBuffer buffer = ByteBuffer.wrap(packBytes);
		channel.send(buffer, address);
	}

}
