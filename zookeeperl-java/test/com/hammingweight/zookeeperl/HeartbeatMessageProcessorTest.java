package com.hammingweight.zookeeperl;

import junit.framework.TestCase;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;

public class HeartbeatMessageProcessorTest extends TestCase {

	private HeartbeatMessageProcessor messageProcessor;
	private OtpErlangPid senderPid;
	
	public void setUp() {
		this.messageProcessor = new HeartbeatMessageProcessor();
	}
	
	public void testResponse() {
		this.messageProcessor.processMessage(this.senderPid, null, new OtpErlangTuple(new OtpErlangObject[0]));
		fail("Not yet done.");
	}
}
