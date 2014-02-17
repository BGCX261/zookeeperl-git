package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangObject;

import junit.framework.TestCase;

public class HeartbeatMessageProcessorTest extends TestCase {

	private HeartbeatMessageProcessor cut;
	
	public void setUp() {
		this.cut = new HeartbeatMessageProcessor();
	}
	
	public void testResponse() {
		OtpErlangObject response = this.cut.processMessage( new OtpErlangObject[0]);
		assertEquals(new OtpErlangAtom("ok"), response);
	}
}
