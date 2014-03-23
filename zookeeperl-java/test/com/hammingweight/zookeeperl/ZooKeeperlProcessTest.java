package com.hammingweight.zookeeperl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangBinary;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;

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
		OtpErlangAtom respType = new OtpErlangAtom("heartbeat");
		OtpErlangTuple respBody = new OtpErlangTuple(respType);
	
		ArgumentCaptor<OtpErlangTuple> arg = ArgumentCaptor.forClass(OtpErlangTuple.class);
		verify(mbox).send(eq(pid), arg.capture());
		OtpErlangTuple resp = arg.getValue();
		assertEquals(uid, resp.elementAt(0));
		assertEquals(respBody, resp.elementAt(1));
		assertEquals(2, resp.arity());
	}

	@Test
	public void testCreateSyncEphemeral() throws Throwable {
		ZooKeeper zookeeper = mock(ZooKeeper.class);
		OtpMbox mbox = mock(OtpMbox.class);
		ZooKeeperlProcess zkProcess = new ZooKeeperlProcess(zookeeper, mbox);
		
		// Expect a create message
		OtpErlangPid pid = mock(OtpErlangPid.class);
		OtpErlangAtom uid = new OtpErlangAtom("uid");
		OtpErlangAtom msgType = new OtpErlangAtom("create_sync");
		OtpErlangString path = new OtpErlangString("/foobar");
		OtpErlangBinary data = new OtpErlangBinary(new byte[7]);
		OtpErlangAtom createMode = new OtpErlangAtom("ephemeral");
		OtpErlangTuple msgBody = new OtpErlangTuple(new OtpErlangObject[]{msgType, path, data, createMode});
		OtpErlangTuple msg = new OtpErlangTuple(new OtpErlangObject[]{pid, uid, msgBody});
		
		when(mbox.receive()).thenReturn(msg);
		when(zookeeper.create(eq("/foobar"), any(byte[].class), eq(Ids.OPEN_ACL_UNSAFE), eq(CreateMode.EPHEMERAL))).thenReturn("/foobar");

		zkProcess.processNextMessage();
		
		verify(mbox).receive();

		// We expect the ZooKeeper process to receive a create message
		verify(zookeeper).create(eq("/foobar"), any(byte[].class), eq(Ids.OPEN_ACL_UNSAFE), eq(CreateMode.EPHEMERAL));
		
		// A create response message should be sent back.
		ArgumentCaptor<OtpErlangTuple> arg = ArgumentCaptor.forClass(OtpErlangTuple.class);
		verify(mbox).send(eq(pid), arg.capture());
		OtpErlangTuple resp = arg.getValue();
		assertEquals(uid, resp.elementAt(0));
		OtpErlangTuple respBody = (OtpErlangTuple) resp.elementAt(1);
		assertEquals(2, respBody.arity());
		assertEquals(msgType, respBody.elementAt(0));
		assertEquals(new OtpErlangString("/foobar"), respBody.elementAt(1));
		assertEquals(2, resp.arity());
	}
}
