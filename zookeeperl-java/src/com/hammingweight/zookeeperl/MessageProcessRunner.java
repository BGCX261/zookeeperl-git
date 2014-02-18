package com.hammingweight.zookeeperl;

import com.ericsson.otp.erlang.OtpMbox;

public class MessageProcessRunner implements Runnable {

	private OtpMbox mailbox;
	private IMessageProcessor messageProcessor;
	
	public MessageProcessRunner(OtpMbox mailbox, IMessageProcessor messageProcessor) {
		this.mailbox = mailbox;
		this.messageProcessor = messageProcessor;
	}
	
	void processNextMessage() {
		
	}
	
	@Override
	public void run() {
		while (true) {
			processNextMessage();
		}
	}

}
