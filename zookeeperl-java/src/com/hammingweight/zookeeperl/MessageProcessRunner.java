package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;

public class MessageProcessRunner implements Runnable {

	private OtpMbox mailbox;
	private ZooKeeperProcess messageProcessor;
	
	public MessageProcessRunner(OtpMbox mailbox, ZooKeeperProcess messageProcessor) {
		this.mailbox = mailbox;
		this.messageProcessor = messageProcessor;
	}
	
	void processNextMessage() {
		try {
			OtpErlangTuple msg = (OtpErlangTuple) this.mailbox.receive();
			OtpErlangObject[] msgElements = msg.elements();
			OtpErlangPid sender = (OtpErlangPid) msgElements[0];
			OtpErlangObject uid = msgElements[1];
			OtpErlangTuple message = new OtpErlangTuple(msgElements, 2, msgElements.length - 2);
			this.messageProcessor.processMessage(sender, uid, message);
		} catch (Throwable t) {
			t.printStackTrace();
			// TODO: log
		}
	}
	
	@Override
	public void run() {
		while (true) {
			processNextMessage();
		}
	}

}
