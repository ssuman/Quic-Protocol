package handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import Segment.Segment;
import server.Connection;
import utils.RequestToBlocks;

public class SlowStartHandler extends CongestionControl implements Handler<RequestToBlocks> {
	Connection con;
	String file;
	List<ByteBuffer> sendBuf;

	public SlowStartHandler(String file, Connection connection, List<ByteBuffer> buf) {
		this.con = connection;
		this.file = file;
		this.sendBuf = buf;
	}

	@Override
	public void handle(RequestToBlocks requestToBlock) throws IOException {
		List<ByteBuffer> blocks = requestToBlock.requestToByteBuffer.get(file);
		
		for (int i = con.SND_UNA; i < con.SND_WIND; i++) {
			
		}
	}

}
