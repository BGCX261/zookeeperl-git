package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;

public class HeartbeatMessageProcessor implements IMessageProcessor {

	@Override
	public void processMessage(OtpErlangPid sender, OtpErlangObject uid, OtpErlangTuple message) {
	}

}
