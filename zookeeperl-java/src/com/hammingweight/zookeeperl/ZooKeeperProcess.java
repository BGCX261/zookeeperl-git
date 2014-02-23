package com.hammingweight.zookeeperl;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangBinary;
import com.ericsson.otp.erlang.OtpErlangFun;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;
import com.hammingweight.zookeeperl.callbacks.CreateCallback;

public class ZooKeeperProcess implements Runnable {

	ZooKeeper zooKeeper;

	private OtpMbox mailbox;

	public ZooKeeperProcess(OtpNode node) {
		this(node.createMbox("zookeeper"));
	}

	ZooKeeperProcess(OtpMbox mailbox) {
		this.mailbox = mailbox;
	}

	void processOpenZooKeeperConnection(final OtpErlangPid sender,
			OtpErlangTuple message) throws Throwable {
		OtpErlangObject[] messageAsArray = message.elements();

		// There should be 3 entries in the tuple
		if (messageAsArray.length != 3) {
			// TODO: log error
			return;
		}

		// The first entry in the tuple must be "open".
		if (!((OtpErlangString) messageAsArray[0]).stringValue().equals("open")) {
			// TODO: log error
			return;
		}

		String connectString = ((OtpErlangString) messageAsArray[1])
				.stringValue();
		int sessionTimeout = ((OtpErlangLong) messageAsArray[2]).intValue();
		Watcher watcher = new Watcher() {

			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				System.out.println("state changed");
				ZooKeeperProcess.this.mailbox.send(sender, new OtpErlangAtom(
						"stateChanged"));
			}

		};
		this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
		System.out.println(this.zooKeeper);
	}

	void create(OtpErlangPid sender, OtpErlangTuple paramsTuple)
			throws Throwable {
		OtpErlangObject[] params = paramsTuple.elements();
		if (params.length != 6) {
			// TODO: log
			return;
		}
		String path = ((OtpErlangString) params[0]).stringValue();
		byte[] data = ((OtpErlangBinary) params[1]).binaryValue();
		// TODO: Extract from param 2
		List<ACL> acl = Ids.OPEN_ACL_UNSAFE;
		// TODO: Extract this from the param 3
		CreateMode createMode = CreateMode.EPHEMERAL;
		// Param 4 contains a fun that will be invoked
		OtpErlangFun fun = (OtpErlangFun) params[4];
		OtpErlangObject ctx = params[5];
		CreateCallback cb = new CreateCallback(this.mailbox, sender, fun);

		this.zooKeeper.create(path, data, acl, createMode, cb, ctx);
	}

	void processZooKeeperMessage(OtpErlangPid sender, String command,
			OtpErlangTuple params) throws Throwable {
		if (command.equals("create")) {
			this.create(sender, params);
		} else {
			// TODO: Log error
		}

	}

	public void processMessage(OtpErlangPid sender, OtpErlangTuple message)
			throws Throwable {
		if (this.zooKeeper == null) {
			// If we haven't instantiated a ZooKeeper yet, the only command
			// we'll accept is a command to create a ZooKeeper.
			this.processOpenZooKeeperConnection(sender, message);
		} else {
			OtpErlangObject[] messageAsTuple = message.elements();
			this.processZooKeeperMessage(
					sender,
					((OtpErlangString) messageAsTuple[0]).stringValue(),
					new OtpErlangTuple(messageAsTuple, 1, messageAsTuple.length));
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				OtpErlangTuple msg = (OtpErlangTuple) this.mailbox.receive();
				OtpErlangObject[] msgElements = msg.elements();
				OtpErlangPid sender = (OtpErlangPid) msgElements[0];
				OtpErlangTuple message = new OtpErlangTuple(msgElements, 1,
						msgElements.length - 1);
				this.processMessage(sender, message);
			} catch (Throwable t) {
				t.printStackTrace();
				// TODO: log
			}
		}
	}

}
