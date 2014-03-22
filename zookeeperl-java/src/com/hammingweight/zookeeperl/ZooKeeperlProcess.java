package com.hammingweight.zookeeperl;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;

public class ZooKeeperlProcess implements Runnable {

	private ZooKeeper zookeeper;
	
	private OtpMbox mbox;
	
	public ZooKeeperlProcess(ZooKeeper zookeeper, OtpMbox mbox) {
		this.zookeeper = zookeeper;
		this.mbox = mbox;
	}
	
	private void processHeartbeat(OtpErlangPid pid, OtpErlangObject uid) {
		OtpErlangTuple resp = new OtpErlangTuple(new OtpErlangObject[] {uid, new OtpErlangTuple(new OtpErlangAtom("heartbeat"))});
		this.mbox.send(pid, resp);
	}
	
	private void processCreateSync(OtpErlangPid pid, OtpErlangObject uid, OtpErlangTuple msgBody) throws Throwable {
		this.zookeeper.create("/foo", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
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
			else if (msgBody.elementAt(0).equals(new OtpErlangAtom("create_sync"))) {
				processCreateSync(pid, uid, msgBody);
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
