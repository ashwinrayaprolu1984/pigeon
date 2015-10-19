package eu.unicredit.reader.thread;

import kafka.consumer.ConsumerIterator;

public class LeaderManagement extends Thread{

	private ConsumerIterator  i;
	
	public LeaderManagement(ConsumerIterator i ){
		this.i=i;
	}
	
	@Override
	public void run() {
		
		
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		
	}

}
