package handler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Segment.Segment;
import utils.RequestToBlocks;

public class GetRequestHandler {

	// BYTE COUNT -block size of each packet
	private static final int BYTE_COUNT = 1500;
	// path of the GET file
	String path;
	// HTTP GET method
	String method;
	// HTTP version
	String schemaAndVersion;
	// Host name
	String origin;
	Map<String, RequestToBlocks> connectionToResponse;

	/**
	 * Calling the constructor for the get request handler.
	 * As a parameter 
	 * 
	 * @param connectionToResponse	ConnectionId -> <File -> blocks>	
	 */
	public GetRequestHandler(Map<String, RequestToBlocks> connectionToResponse) {
		this.connectionToResponse = connectionToResponse;
	}

	public GetRequestHandler() {

	}

	

	/**
	 * Handle the get request by parsing the segment data.
	 * Also, read the file and make blocks. 
	 * Maintain a fileID -> list of blocks map.
	 * @param seg
	 * @return
	 * @throws IOException
	 */
	public String handle(Segment seg) throws IOException {
		parseReq(seg.data);
		List<ByteBuffer> list = readFileAndMakeBlocks();
		RequestToBlocks  req = new RequestToBlocks(new HashMap<>());
		req.requestToByteBuffer.put(path, list);
		connectionToResponse.put(seg.connectionId, req);
		return path;
	}

	/***
	 * Read from path and make blocks from the file. Store it into a Hashmap so
	 * that congestion control can send it according to the CWND.
	 * 
	 * @return ByteBuffer
	 * @throws IOException
	 */
	private List<ByteBuffer> readFileAndMakeBlocks() throws IOException {

		RandomAccessFile aFile = new RandomAccessFile(path, "rw");
		FileChannel fChannel = aFile.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(BYTE_COUNT);
		List<ByteBuffer> queue = new ArrayList<>();
		int bytesRead = 0;
		while ((bytesRead = fChannel.read(buffer)) != -1) {
			buffer.flip();
			byte[] bytes = new byte[BYTE_COUNT];
			buffer.get(bytes, 0, buffer.limit());
			ByteBuffer newBuffer = ByteBuffer.wrap(bytes);
			queue.add(newBuffer);
			buffer.clear();

		}
		fChannel.close();
		aFile.close();
		return queue;
	}
	
	
	

	/**
	 * Parse the GET request
	 * 
	 * @param data
	 */
	private void parseReq(byte[] data) {
		String str = new String(data, StandardCharsets.UTF_8);
		String[] arr = str.split("\n");
		String[] firstLine = arr[0].split(" ");
		String method = firstLine[0];
		String path = firstLine[1];
		String schemaAndVersion = firstLine[2];
		this.path = path;
		this.method = method;
		this.schemaAndVersion = schemaAndVersion;
		//TODO: Create other header fields too.
		/*
		 * for(int i=1; i< arr.length; i++){ if() }
		 */
	}
	
	
	/**
	 * Testing the applications.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Map<String, RequestToBlocks> connectionToResponse = new HashMap<>();
		GetRequestHandler get = new GetRequestHandler(connectionToResponse);
		Segment seg = new Segment();
		String str = "GET hello.txt HTTP/1.1\n";
		str+="HEADER1: ad";
		seg.data = str.getBytes();
		get.handle(seg);
		List<ByteBuffer> buf = get.readFileAndMakeBlocks();
		FileChannel fos = new FileOutputStream("name.txt").getChannel();
		for (ByteBuffer b : buf) {
			fos.write(b);
			
		}
		fos.close();
		
	}

}
