package com.hammingweight.zookeeperl;

import org.apache.zookeeper.ZooKeeper;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpOutputStream;

public class ZooKeeperlProcess implements Runnable {

	private ZooKeeper zookeeper;
	
	private OtpMbox mbox;
	
	public ZooKeeperlProcess(ZooKeeper zookeeper, OtpMbox mbox) {
		this.zookeeper = zookeeper;
		this.mbox = mbox;
	}
	
	void processNextMessage() {
		try {
			OtpErlangTuple msg = (OtpErlangTuple) this.mbox.receive();
			OtpErlangPid pid = (OtpErlangPid) msg.elementAt(0);
			this.mbox.send(pid, new OtpErlangObject() {
				
				@Override
				public String toString() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public boolean equals(Object arg0) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public void encode(OtpOutputStream arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
		} catch (Throwable t) {
			// TODO Auto-generated catch block
			t.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (true) {
			processNextMessage();
		}

	}

}
