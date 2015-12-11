package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Segment.Segment;
import server.Flags;

public class TCPClient {

	private final static String SERVER_ADDRESS = "129.21.37.16";
	private final static int SERVER_PORT = 8001;

	private static Map<DatagramChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
	static String connectionId = UUID.randomUUID().toString();
	static SocketAddress address;
	static InetAddress localAddress;

	public static void main(String[] args) throws IOException, InterruptedException {

		TCPClient client = new TCPClient();
		// TODO : condition check.
		if (args.length != 2) {
			System.out.println("Usage TCPClient <filename> <output>");
			return;
		}
		String filename = args[0];
		String output = args[1];
		ExecutorService service = Executors.newFixedThreadPool(10);
		localAddress = InetAddress.getLocalHost();
		
		DatagramChannel channel = DatagramChannel.open();
		channel.configureBlocking(false);
		// Create a background Thread for the checking Ip Address changes.
		service.submit(() -> {
			// Threads checks for the changes every 500 milliseconds.
			while (true) {
				InetAddress newAddr = InetAddress.getLocalHost();
				InetAddress addr = InetAddress.getLoopbackAddress();
				if (!newAddr.equals(addr) && !localAddress.equals(newAddr)) {
					System.out.println("Old Addr" + localAddress);
					System.out.println("New Addr" + newAddr);
					System.out.flush();
					// If the ip address has changed send a PING request.
					// And send the last seen sequence number.
					Segment segment = new Segment();
					Collections.sort(ClientWriteHandler.blocks);
					// if there are no acknowledgements. then ignore.

					if (ClientWriteHandler.blocks.size() > 0)
						segment.acknowledgementNbr = ClientWriteHandler.blocks
								.get(ClientWriteHandler.blocks.size() - 1).seqNbr;

					localAddress = newAddr;
					// Use the same connection ID.
					segment.connectionId = connectionId;
					segment.filename = filename;
					segment.NACKs = new ArrayList<>();
					// Use the Flag as Flags.ACK
					segment.flag = Flags.ACK;
					byte[] bytes = Segment.serializeToBytes(segment);
					ByteBuffer buffer = ByteBuffer.wrap(bytes);
					// send it to the server.
					channel.send(buffer, address);
				}
				Thread.sleep(500);
			}
		});
		address = new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT);
		Selector selector = Selector.open();
		channel.register(selector, SelectionKey.OP_READ);
		client.sendCHLO(channel, filename);
		System.out.println("CHLO sent");
		client.sendRequest(channel, filename);
		client.receiveRequest(channel, selector, filename, output);
		
		

	}

	/***
	 * Send GET Requests to Server.
	 * 
	 * @param channel
	 *            Channel for the connection.
	 * @param filename
	 * @throws IOException
	 */
	private void sendRequest(DatagramChannel channel, String filename) throws IOException {
		Segment seg = new Segment();
		seg.flag = Flags.REQ;
		String str = "GET " + filename + " HTTP/1.1\n";
		System.out.println(str);
		seg.data = (str).getBytes();
		seg.sequenceNbr = 0;
		seg.connectionId = connectionId;
		seg.filename = "hello.txt";
		byte[] bytes = Segment.serializeToBytes(seg);
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		channel.send(buffer, address);

	}

	/***
	 * Sending CHLO Requests.
	 * 
	 * @param channel
	 *            Channel for the connection.
	 * @param filename
	 *            filename
	 * @throws IOException
	 *             throws IOException.
	 */
	private void sendCHLO(DatagramChannel channel, String filename) throws IOException {
		Segment seg = new Segment();
		seg.sequenceNbr = 0;
		seg.flag = Flags.CHLO;
		seg.connectionId = connectionId;
		seg.filename = filename;
		byte[] bytes = Segment.serializeToBytes(seg);
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		channel.send(buffer, address);
	}

	/***
	 * Receive requests from the server.
	 * 
	 * @param channel
	 *            channel for the connection.
	 * @param selector
	 *            selector to select keys.
	 * @param filename
	 *            input file name.
	 * @param output
	 *            output file name.
	 * @throws IOException
	 * @throws ClosedChannelException
	 * @throws InterruptedException
	 */
	private void receiveRequest(DatagramChannel channel, Selector selector, String filename, String output)
			throws IOException, ClosedChannelException, InterruptedException {
		while (true) {
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iter = keys.iterator();
			Thread.sleep(100);
			while (iter.hasNext()) {
				SelectionKey key = iter.next();
				iter.remove();
				if (key.isReadable()) {
					new ClientReadHandler(pendingData).handle(key);
				} else if (key.isWritable()) {
					new ClientWriteHandler(pendingData, filename, output).handle(key);
				}
			}
		}
	}
}
