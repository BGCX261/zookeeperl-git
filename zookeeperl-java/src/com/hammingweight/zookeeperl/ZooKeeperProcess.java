package com.hammingweight.zookeeperl;

import org.apache.zookeeper.ZooKeeper;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;

public class ZooKeeperProcess implements IMessageProcessor {

	ZooKeeper zooKeeper;
	
	@Override
	public void processMessage(OtpErlangPid sender, OtpErlangObject uid,
			OtpErlangTuple message) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public OtpMbox getMbox() {
		// TODO Auto-generated method stub
		return null;
	}

}
