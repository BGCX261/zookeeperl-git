package com.hammingweight.zookeeperl;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangBinary;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;

public class ZooKeeperlProcess implements Runnable {

	private ZooKeeper zookeeper;
	
	private OtpMbox mbox;
	
	public ZooKeeperlProcess(ZooKeeper zookeeper, OtpMbox mbox) {
		this.zookeeper = zookeeper;
		this.mbox = mbox;
	}
	
	private void processHeartbeat(OtpErlangPid pid, OtpErlangObject uid, OtpErlangTuple msgBody) {
		OtpErlangAtom command = (OtpErlangAtom) msgBody.elementAt(0);
		OtpErlangTuple resp = new OtpErlangTuple(new OtpErlangObject[] {uid, new OtpErlangTuple(command)});
		this.mbox.send(pid, resp);
	}
	
	private void processCreateSync(OtpErlangPid pid, OtpErlangObject uid, OtpErlangTuple msgBody) throws Throwable {
		OtpErlangString path = (OtpErlangString) msgBody.elementAt(1);
		OtpErlangBinary data = (OtpErlangBinary) msgBody.elementAt(2);
		this.zookeeper.create(path.stringValue(), data.binaryValue(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	}
	
	void processNextMessage() {
		try {
			OtpErlangTuple msg = (OtpErlangTuple) this.mbox.receive();
			OtpErlangPid pid = (OtpErlangPid) msg.elementAt(0);
			OtpErlangObject uid = msg.elementAt(1);
			OtpErlangTuple msgBody = (OtpErlangTuple) msg.elementAt(2);
			if (msgBody.elementAt(0).equals(new OtpErlangAtom("heartbeat"))) {
				processHeartbeat(pid, uid, msgBody);
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
