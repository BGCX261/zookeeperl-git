package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpErlangObject;

public interface IMessageProcessor {
	
	public OtpErlangObject processMessage(OtpErlangObject[] message) throws Throwable;

}
