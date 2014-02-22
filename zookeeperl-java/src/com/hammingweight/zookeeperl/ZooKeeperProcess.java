package com.hammingweight.zookeeperl;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;

public class ZooKeeperProcess implements Runnable {

	ZooKeeper zooKeeper;
	
	private OtpMbox mailbox;
	
	public ZooKeeperProcess(OtpNode node) {
		this(node.createMbox("zookeeper"));
	}
	
	public ZooKeeperProcess(OtpMbox mailbox) {
		this.mailbox = mailbox;
	}
	
	void processInstantiateZooKeeper(final OtpErlangPid sender, OtpErlangObject uid,
			OtpErlangTuple message) throws Throwable {
		OtpErlangObject[] messageAsArray = message.elements();

		// There should be 3 entries in the tuple
		if (messageAsArray.length != 3) {
			// TODO: log error
			return;
		}

		// The first entry in the tuple must be "open".
		if (!((OtpErlangString)messageAsArray[0]).stringValue().equals("open")) {
			// TODO: log error
			return;
		}
		
		String connectString = ((OtpErlangString)messageAsArray[1]).stringValue();
		int sessionTimeout = ((OtpErlangLong)messageAsArray[2]).intValue();
		Watcher watcher = new Watcher() {

			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				System.out.println("state changed");
				ZooKeeperProcess.this.mailbox.send(sender, new OtpErlangAtom("stateChanged"));
			}
			
		};
		this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
		System.out.println(this.zooKeeper);
	}
	
	public void processMessage(OtpErlangPid sender, OtpErlangObject uid,
			OtpErlangTuple message) throws Throwable {
		if (this.zooKeeper == null) {
			// If we haven't instantiated a ZooKeeper yet, the only command
			// we'll accept is a command to create a ZooKeeper.
			System.out.println(uid);
			this.processInstantiateZooKeeper(sender, uid, message);
		} else {
			
		}
	}

	public OtpMbox getMbox() {
		return this.mailbox;
	}

	
	@Override
	public void run() {
		while (true) {
			try {
				OtpErlangTuple msg = (OtpErlangTuple) this.mailbox.receive();
				OtpErlangObject[] msgElements = msg.elements();
				OtpErlangPid sender = (OtpErlangPid) msgElements[0];
				OtpErlangObject uid = msgElements[1];
				OtpErlangTuple message = new OtpErlangTuple(msgElements, 2, msgElements.length - 2);
				this.processMessage(sender, uid, message);
			} catch (Throwable t) {
				t.printStackTrace();
				// TODO: log
			}
		}
	}

}
