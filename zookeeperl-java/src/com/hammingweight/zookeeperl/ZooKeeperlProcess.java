package com.hammingweight.zookeeperl;

import org.apache.zookeeper.ZooKeeper;

import com.ericsson.otp.erlang.OtpErlangAtom;
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
	
	private void processHeartbeat(OtpErlangPid pid, OtpErlangObject uid) {
		OtpErlangTuple resp = new OtpErlangTuple(new OtpErlangObject[] {uid, new OtpErlangTuple(new OtpErlangAtom("heartbeat_response"))});
		this.mbox.send(pid, resp);
	}
	
	void processNextMessage() {
		try {
			OtpErlangTuple msg = (OtpErlangTuple) this.mbox.receive();
			OtpErlangPid pid = (OtpErlangPid) msg.elementAt(0);
			OtpErlangObject uid = msg.elementAt(1);
			OtpErlangTuple msgBody = (OtpErlangTuple) msg.elementAt(2);
			if (msgBody.elementAt(0).equals(new OtpErlangAtom("heartbeat"))) {
				processHeartbeat(pid, uid);
			}
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
