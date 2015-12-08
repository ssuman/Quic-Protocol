package client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import Segment.Segment;
import handler.Handler;
import server.Flags;

public class ClientWriteHandler implements Handler<SelectionKey> {

	private final Map<DatagramChannel, Queue<ByteBuffer>> pendingData;
	final static List<Blocks> blocks = new ArrayList<>();
	String filename;
	String outputFile;
	
	public ClientWriteHandler(Map<DatagramChannel, Queue<ByteBuffer>> pendingData, String filename,
			String outputFile) {
		this.pendingData = pendingData;
		this.filename = filename;
		this.outputFile = outputFile;
	}

	public ClientWriteHandler() {
		this.pendingData = new HashMap<>();
	}

	@Override
	public void handle(SelectionKey key) throws IOException {
		DatagramChannel channel = (DatagramChannel) key.channel();
		Queue<ByteBuffer> queue = pendingData.get(channel);
		SocketAddress address = (SocketAddress) key.attachment();
		System.out.println(address);
		ByteBuffer buffer = queue.poll();
		System.out.println(buffer.position());
		System.out.println(buffer.limit());
		Segment segment = null;
		try {
			segment = Segment.deserializeBytes(buffer);

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}

		switch (segment.flag) {

		case SHLO:
			onSHLO(channel, segment, address, key);
			break;
		case DATA:
			onDATA(channel, segment, address, key);
			break;
		case FIN:
			onFIN(channel, segment, address, key);
			break;
		default:
			break;

		}
		key.interestOps(SelectionKey.OP_READ);
	}

	private void onFIN(DatagramChannel channel, Segment segment, SocketAddress address, SelectionKey key)
			throws IOException {
		// TODO : Check if there is no gap
		Collections.sort(blocks);
		FileChannel fos = new FileOutputStream(outputFile).getChannel();
		for (Blocks b : blocks) {
			fos.write(b.buffer);
		}
		fos.close();
		System.exit(0);

	}

	private void onDATA(DatagramChannel channel, Segment segment, SocketAddress address, SelectionKey key)
			throws IOException {
		byte[] data = segment.data;

		ByteBuffer buffer = ByteBuffer.wrap(data);
		blocks.add(new Blocks(segment.sequenceNbr, buffer));
		Collections.sort(blocks);
		Segment seg = new Segment();
		seg.flag = Flags.ACK;
		seg.connectionId = TCPClient.connectionId;
		seg.NACKs = findNacks();
		seg.filename = filename;
		seg.acknowledgementNbr = blocks.get(blocks.size() - 1).seqNbr;
		byte[] bytes = Segment.serializeToBytes(seg);
		ByteBuffer sendBuf = ByteBuffer.wrap(bytes);
		channel.send(sendBuf, address);

	}

	private List<Integer> findNacks() {
		List<Integer> temp = new ArrayList<>();
		for (int i = 1; i < blocks.size(); i++) {
			int seq1 = blocks.get(i - 1).seqNbr;
			int seq2 = blocks.get(i).seqNbr;
			if ((seq2 - seq1) > 1) {
				for (int j = seq1 + 1; j < seq2; j++) {
					temp.add(j);
				}
			}
		}
		return temp;
	}

	private void onSHLO(DatagramChannel channel, Segment segment, SocketAddress address, SelectionKey key) {
		System.out.println("Server SHLO received!");
	}

}
