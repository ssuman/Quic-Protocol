package utils;

public class RttStats {

	private final static double ALPHA = 0.125;
	private final static double ONE_MINUS_ALPHA = 1 - ALPHA;
	private final static double BETA = 0.25;
	private final static double ONE_MINUS_BETA = 1 - BETA;

	private double latestRtt;
	private double minRtt;
	private double smoothedRtt;
	private double meanDeviation;
	private double retransmissionTimeout;

	public RttStats() {

		latestRtt = 0;
		minRtt = 0;
		smoothedRtt = 0;
		meanDeviation = 0;
		retransmissionTimeout = 1;
	}
	
	public double getRetransmissionTimeout(){
		return this.retransmissionTimeout;
	}
	
	public void setRetransmissionTimeout(double val){
		this.retransmissionTimeout = val;
	}
	

	public void updateRtt(double newRtt) {
		latestRtt = newRtt;
		minRtt = Math.min(minRtt, newRtt);
		if (smoothedRtt == 0) {
			
			smoothedRtt = newRtt;
			meanDeviation = newRtt / 2.0;
			
		} else {
			meanDeviation = ONE_MINUS_BETA * meanDeviation 
					+ BETA * Math.abs(smoothedRtt - newRtt);
			smoothedRtt = ONE_MINUS_ALPHA * smoothedRtt + ALPHA * newRtt;
		}
		
		retransmissionTimeout = smoothedRtt + 4 * meanDeviation;
		if(retransmissionTimeout < 1) 
			retransmissionTimeout = 1;
	}
	
	public void updateRttAfterExpire(){
		retransmissionTimeout *= 2;
		smoothedRtt = 0;
		meanDeviation = 0;
	}
}
