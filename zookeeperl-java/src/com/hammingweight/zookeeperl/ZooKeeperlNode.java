package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;

public class ZooKeeperlNode {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		OtpNode node = new OtpNode("zookeeper");
		OtpMbox mbox = node.createMbox("heartbeat");
		
		ZooKeeperProcess zookeeper = new ZooKeeperProcess(node);
		MessageProcessRunner zookeeperRunner = new MessageProcessRunner(zookeeper.getMbox(), zookeeper);
		(new Thread(zookeeperRunner)).start();
		
		HeartbeatProcessor heartbeat = new HeartbeatProcessor(mbox);
		MessageProcessRunner heartbeatRunner = new MessageProcessRunner(mbox, heartbeat);
		Thread t = new Thread(heartbeatRunner);
		t.start();
	}

}
