package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;

public interface IMessageProcessor {
	
	public void processMessage(OtpErlangPid sender, OtpErlangObject[] message) throws Throwable;

}
