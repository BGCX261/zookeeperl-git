package com.hammingweight.zookeeperl;

import junit.framework.TestCase;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;

public class HeartbeatProcessorTest extends TestCase {

	private HeartbeatProcessor messageProcessor;
	private OtpErlangPid senderPid;
	
	public void setUp() {
		// Create a message processor with a mocked mailbox
		this.messageProcessor = new HeartbeatProcessor((OtpMbox) null);
	}
	
	public void testResponse() {
		this.messageProcessor.processMessage(this.senderPid, null, new OtpErlangTuple(new OtpErlangObject[0]));
		fail("Not yet done.");
	}
}
