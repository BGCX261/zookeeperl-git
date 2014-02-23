package com.hammingweight.zookeeperl.callbacks;

import com.ericsson.otp.erlang.OtpErlangFun;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpMbox;

public abstract class Callback {

	OtpMbox mailbox;
	OtpErlangPid pid;
	OtpErlangFun fun;
	
	public Callback(OtpMbox mailbox, OtpErlangPid pid, OtpErlangFun fun) {
		this.mailbox = mailbox;
		this.pid = pid;
		this.fun = fun;
	}
}
