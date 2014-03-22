package com.hammingweight.zookeeperl;

import static org.junit.Assert.*;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

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
		
		// We expect the process to send back a heartbeat_response
		OtpErlangAtom respType = new OtpErlangAtom("heartbeat_response");
		OtpErlangTuple respBody = new OtpErlangTuple(respType);
	
		ArgumentCaptor<OtpErlangTuple> arg = ArgumentCaptor.forClass(OtpErlangTuple.class);
		verify(mbox).send(eq(pid), arg.capture());
		OtpErlangTuple resp = arg.getValue();
		assertEquals(uid, resp.elementAt(0));
		assertEquals(respBody, resp.elementAt(1));
		assertEquals(2, resp.arity());
	}

}
