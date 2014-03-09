package com.hammingweight.zookeeperl;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import com.ericsson.otp.erlang.OtpErlangBinary;
import com.ericsson.otp.erlang.OtpErlangFun;
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

	public ZooKeeperProcess(OtpNode node, String connectString, int sessionTimeout) throws Throwable {
		this(node.createMbox("mbox"), connectString, sessionTimeout);
		System.out.println(node.node());
	}

	ZooKeeperProcess(OtpMbox mailbox, String connectString, int sessionTimeout) throws Throwable {
		this.mailbox = mailbox;
		Watcher watcher = new Watcher() {

			@Override
			public void process(WatchedEvent event) {
				// TODO log
			}

		};
		this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
	}

	void create(OtpErlangPid sender, OtpErlangTuple paramsTuple)
			throws Throwable {
		OtpErlangObject[] params = paramsTuple.elements();
		if (params.length != 5) {
			// TODO: log
			return;
		}
		String path = ((OtpErlangString) params[0]).stringValue();
		byte[] data = ((OtpErlangBinary) params[1]).binaryValue();
		List<ACL> acl = Ids.OPEN_ACL_UNSAFE;
		// TODO: Extract this from the param 2
		CreateMode createMode = CreateMode.EPHEMERAL;
		// Param 3 contains a fun that will be invoked
		OtpErlangFun fun = (OtpErlangFun) params[3];
		OtpErlangObject ctx = params[4];
		CreateCallback cb = new CreateCallback(this.mailbox, sender, fun);

		this.zooKeeper.create(path, data, acl, createMode, cb, ctx);
	}

	void createSync(OtpErlangPid sender, OtpErlangTuple paramsTuple)
			throws Throwable {
		OtpErlangObject[] params = paramsTuple.elements();
		if (params.length != 4) {
			// TODO: log
			return;
		}
		OtpErlangObject uid = params[0];
		String path = ((OtpErlangString) params[1]).stringValue();
		byte[] data = ((OtpErlangBinary) params[2]).binaryValue();
		List<ACL> acl = Ids.OPEN_ACL_UNSAFE;
		// TODO: Extract this from the param 3
		CreateMode createMode = CreateMode.EPHEMERAL;

		String response = this.zooKeeper.create(path, data, acl, createMode);
		this.mailbox.send(sender, new OtpErlangTuple(new OtpErlangObject[]{uid, new OtpErlangString(response)}));
	}

	void processZooKeeperMessage(OtpErlangPid sender, String command,
			OtpErlangTuple params) throws Throwable {
		if (command.equals("create")) {
			this.create(sender, params);
		} else if (command.equals("create-sync")) {
			this.createSync(sender, params);
		} else {
			// TODO: Log error
		}

	}

	public void processMessage(OtpErlangPid sender, OtpErlangTuple message)
			throws Throwable {
		OtpErlangObject[] messageAsTuple = message.elements();
		this.processZooKeeperMessage(
				sender,
				((OtpErlangString) messageAsTuple[0]).stringValue(),
				new OtpErlangTuple(messageAsTuple, 1, messageAsTuple.length-1));
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
