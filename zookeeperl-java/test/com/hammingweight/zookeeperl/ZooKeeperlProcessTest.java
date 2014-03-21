package com.hammingweight.zookeeperl;

import static org.junit.Assert.*;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;

import static org.mockito.Mockito.*;

public class ZooKeeperlProcessTest {

	@Test
	public void testProcessNullMessage() throws Throwable {
		ZooKeeper zookeeper = mock(ZooKeeper.class);
		OtpMbox mbox = mock(OtpMbox.class);
		ZooKeeperlProcess zkProcess = new ZooKeeperlProcess(zookeeper, mbox);

		zkProcess.processNextMessage();
		
		verify(mbox).receive();
	}
	
	@Test
	public void testHeartbeatMessage() throws Throwable {
		ZooKeeper zookeeper = mock(ZooKeeper.class);
		OtpMbox mbox = mock(OtpMbox.class);
		ZooKeeperlProcess zkProcess = new ZooKeeperlProcess(zookeeper, mbox);
		
		// Expect a heartbeat message
		OtpErlangPid pid = mock(OtpErlangPid.class);
		OtpErlangAtom uid = new OtpErlangAtom("uid");
		OtpErlangAtom msgType = new OtpErlangAtom("heartbeat");
		OtpErlangTuple msgBody = new OtpErlangTuple(msgType);
		OtpErlangTuple msg = new OtpErlangTuple(new OtpErlangObject[]{pid, uid, msgBody});
		
		when(mbox.receive()).thenReturn(msg);

		zkProcess.processNextMessage();
		
		verify(mbox).receive();
		verify(mbox).send(eq(pid), isA(OtpErlangObject.class));
	}

}
