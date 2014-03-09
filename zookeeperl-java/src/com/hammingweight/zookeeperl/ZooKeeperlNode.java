package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpNode;

public class ZooKeeperlNode {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable {
		OtpNode node = new OtpNode("zookeeper");
		ZooKeeperProcess zookeeper = new ZooKeeperProcess(node, "localhost", 30000);
		(new Thread(zookeeper)).start();
	}

}
