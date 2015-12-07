package handler;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Segment.Segment;
import server.Connection;
import server.Flags;
import utils.RequestToBlocks;

public class SlowStartHandler extends CongestionControl implements Handler<RequestToBlocks> {
	Connection con;
	String file;
	List<Segment> sendBuf;

	/***
	 * Slow start handler constructor.
	 * @param file			name of the file being served.
	 * @param connection	Contains the connection id.
	 * @param buf			List of byte buffers for a particular file.
	 */
	public SlowStartHandler(String file, Connection connection, List<Segment> buf) {
		this.con = connection;
		this.file = file;
		this.sendBuf = buf;
	}
	
	/**
	 * Only for testing purpose. Need to change it.
	 */
	public SlowStartHandler(){
		this.con = new Connection();
		this.file = "123";
		this.sendBuf = new ArrayList<>();
	}

	/**
	 * Send blocks from Send un-acknowledged pointer to send window size pointer.
	 */
	@Override
	public void handle(RequestToBlocks requestToBlock) throws IOException {
		List<ByteBuffer> blocks = requestToBlock.requestToByteBuffer.get(file);
		for (int i = con.SND_UNA; i < blocks.size() && i < con.SND_WIND; i++) {
			if(!requestToBlock.ackedPack.containsKey(i) && !requestToBlock.unAckedPack.containsKey(i)){
				Segment seg = new Segment();
				seg.sequenceNbr = i;
				seg.flag =Flags.DATA;
				seg.data = blocks.get(i).array();
				sendBuf.add(seg);
			}
		}
	}
	
	/**
	 * Only used for testing purpose.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		SlowStartHandler slow = new SlowStartHandler();	
		//slow.con.updateSenderWindow(0, 3);
		Map<String, List<ByteBuffer>> map = new HashMap<>();
		List<ByteBuffer> l = new ArrayList<>();
		l.add(ByteBuffer.wrap("Hi".getBytes()));
		l.add(ByteBuffer.wrap("Hello".getBytes()));
		map.put(slow.file, l);
		RequestToBlocks requestToBlock = new RequestToBlocks(map);
		slow.handle(requestToBlock);
		System.out.println(slow.sendBuf);
	}

}
