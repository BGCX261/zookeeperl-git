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
	
	private void processHeartbeat(OtpErlangPid pid, OtpErlangObject uid) {
		OtpErlangAtom okResp = new OtpErlangAtom("ok");
		OtpErlangTuple resp = new OtpErlangTuple(new OtpErlangObject[] {uid, new OtpErlangTuple(okResp)});
		this.mbox.send(pid, resp);
	}
	
	private void processCreateSync(OtpErlangPid pid, OtpErlangObject uid, OtpErlangTuple msgBody) throws Throwable {
		OtpErlangString path = (OtpErlangString) msgBody.elementAt(1);
		OtpErlangBinary data = (OtpErlangBinary) msgBody.elementAt(2);
		CreateMode createMode = CreateMode.EPHEMERAL;
		if (msgBody.elementAt(3).equals(new OtpErlangAtom("persistent"))) {
			createMode = CreateMode.PERSISTENT;
		}
		OtpErlangString respPath = new OtpErlangString(zookeeper.create(path.stringValue(), data.binaryValue(), Ids.OPEN_ACL_UNSAFE, createMode));
		OtpErlangTuple resp = new OtpErlangTuple(new OtpErlangObject[] {uid, new OtpErlangTuple(new OtpErlangObject[]{new OtpErlangAtom("ok"), respPath})});
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
