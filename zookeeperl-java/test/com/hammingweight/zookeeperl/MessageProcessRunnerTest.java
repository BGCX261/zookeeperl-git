package com.hammingweight.zookeeperl;

import junit.framework.TestCase;

public class MessageProcessRunnerTest extends TestCase {

	public void testBadPid() {
		// Test a message where the first element is not a PID
		fail();
	}
	
	public void testGoodMessage() {
		// Test with a mocked IMessageProcess that the message is correctly invoked.
		fail();
	}
	
	public void testMessageProcessorThrowsException() {
		// The message processor throwing an exception shouldn't crash the node.
		fail();
	}
}
