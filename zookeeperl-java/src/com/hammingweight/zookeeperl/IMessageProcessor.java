package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;

public interface IMessageProcessor {
	
	public void processMessage(OtpErlangPid sender, OtpErlangObject uid, OtpErlangTuple message) throws Throwable;
	
	public OtpMbox getMbox();

}
