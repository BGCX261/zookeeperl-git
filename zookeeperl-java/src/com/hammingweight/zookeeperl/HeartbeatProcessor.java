package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;

public class HeartbeatProcessor implements IMessageProcessor {

	private OtpMbox mailbox;
	
	HeartbeatProcessor(OtpMbox mailbox) {
		this.mailbox = mailbox;
	}
	
	public HeartbeatProcessor(OtpNode node) {
		this(node.createMbox("heartbeat"));
	}
	
	
	@Override
	public void processMessage(OtpErlangPid sender, OtpErlangObject uid, OtpErlangTuple message) {
		this.mailbox.send(sender, new OtpErlangAtom("ok"));
	}

	@Override
	public OtpMbox getMbox() {
		return this.mailbox;
	}

}