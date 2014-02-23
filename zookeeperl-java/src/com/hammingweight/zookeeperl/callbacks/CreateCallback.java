package com.hammingweight.zookeeperl.callbacks;

import org.apache.zookeeper.AsyncCallback.StringCallback;

import com.ericsson.otp.erlang.OtpErlangFun;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;

public class CreateCallback extends Callback implements StringCallback {

	public CreateCallback(OtpMbox mailbox, OtpErlangPid pid, OtpErlangFun fun) {
		super(mailbox, pid, fun);
	}
	
	@Override
	public void processResult(int rc, String path, Object ctx, String name) {
		OtpErlangObject[] response = new OtpErlangObject[]{this.fun, new OtpErlangString(path),
				(OtpErlangObject) ctx, new OtpErlangString(name)};
		
		// TODO Auto-generated method stub
		this.mailbox.send(this.pid, new OtpErlangTuple(response));
	}

}
