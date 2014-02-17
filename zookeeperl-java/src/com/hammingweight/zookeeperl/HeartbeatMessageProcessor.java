package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangObject;

public class HeartbeatMessageProcessor implements IMessageProcessor {

	@Override
	public OtpErlangObject processMessage(OtpErlangObject[] message) {
		return new OtpErlangAtom("ok");
	}

}
