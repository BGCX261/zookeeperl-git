package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;

public class HeartbeatMessageProcessor implements IMessageProcessor {

	private OtpMbox mailbox;
	
	public HeartbeatMessageProcessor(OtpMbox mailbox) {
		this.mailbox = mailbox;
	}
	
	public HeartbeatMessageProcessor(OtpNode node) {
		this(node.createMbox("heartbeat"));
	}
	
	
	
	@Override
	public void processMessage(OtpErlangPid sender, OtpErlangObject uid, OtpErlangTuple message) {
		this.mailbox.send(sender, new OtpErlangAtom("ok"));
	}

}
