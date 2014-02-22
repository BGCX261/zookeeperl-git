package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpNode;

public class ZooKeeperlNode {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		OtpNode node = new OtpNode("zookeeper");
		ZooKeeperProcess zookeeper = new ZooKeeperProcess(node);
		(new Thread(zookeeper)).start();
	}

}
