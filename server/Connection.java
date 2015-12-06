package server;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Connection {
	
	public SocketAddress address;
	int windowUpdate;
	public int SSTHRESH = 5000;
	public int CWND = 1024;
	public int SND_UNA=0;
	public int SND_CWND = CWND / 1024;
	public int SND_WIND = SND_UNA + SND_CWND;
	
	public void updateSenderWindow(int SND_UNA, int SND_CWND){
		this.SND_WIND = SND_UNA + SND_CWND;
	}
	
	public Connection(SocketAddress address, int windowUpdate) {
		super();
		this.address = address;
		this.windowUpdate = windowUpdate;
	}

	public void ipAddrChanged(SocketAddress remote){
		
		if(!this.address.equals(remote)){
			System.out.println("Ip Address Changed!");
			this.address = remote;
		}
	}

}
