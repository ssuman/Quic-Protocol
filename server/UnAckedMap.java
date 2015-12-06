package server;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import utils.RttStats;
import utils.Timer;

public class UnAckedMap {
	
	static Map<Integer, Integer> unAckMap;
	static RttStats rtt;
	static Timer timer;
	
	public UnAckedMap(){
		unAckMap = new ConcurrentHashMap<>();
		rtt = new RttStats();
		timer = new Timer();
	}
	
	public List<Integer> getExpiredPackets(long curr_time) {
		
		List<Integer> sequenceNbr = new ArrayList<>();
		for(Map.Entry<Integer,Integer> entry : unAckMap.entrySet()) {
			double curr_rtt = rtt.getRetransmissionTimeout();
			long timeDiff = timer.differnce(entry.getValue(), curr_time);
			if(timeDiff > curr_rtt) {
				sequenceNbr.add(entry.getKey());
			}
			
		}
		return sequenceNbr;
	}
	
	public void removeAckedPackets(int seqNbr){
		
		for(Map.Entry<Integer, Integer> entry: unAckMap.entrySet()){
			if(entry.getKey() <= seqNbr){
				unAckMap.remove(entry.getKey());
			}
		}
		
	}
	
	
	public static void main(String args[]){
		// For testing
		UnAckedMap map = new UnAckedMap();
		UnAckedMap.rtt = new RttStats();
		UnAckedMap.rtt.setRetransmissionTimeout(20000);;
		UnAckedMap.unAckMap.put(1, 10000);
		UnAckedMap.unAckMap.put(2, 20000);
		UnAckedMap.unAckMap.put(3, 30000);
		UnAckedMap.unAckMap.put(4, 40000);
		
		map.removeAckedPackets(2);
		List<Integer> m = map.getExpiredPackets(50000);
		System.out.println(m);
	}

}
