package com.hammingweight.zookeeperl;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;

public class ZooKeeperlNode {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable {
		OtpNode node = new OtpNode("zookeeper");
		OtpMbox mbox = node.createMbox("mbox");
		Watcher watcher = new Watcher() {

			@Override
			public void process(WatchedEvent event) {
				// TODO log
				System.out.println(event);
			}

		};
		ZooKeeper zookeeper = new ZooKeeper(args[0], 30000, watcher);
		ZooKeeperlProcess proc = new ZooKeeperlProcess(zookeeper, mbox);
		(new Thread(proc)).start();
	}

}
