package kr.ac.catholiccs.rshell.server.handler;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static kr.ac.catholiccs.RShellServerApp.logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;

import kr.ac.catholiccs.rshell.server.CommandService;

/**
 * @since 2018/04/28 12:14:18
 *
 */
public class ServerHandler extends IoHandlerAdapter {

	@Autowired
	CommandService commandService;
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("exceptionCaught :" + session.getRemoteAddress().toString() + " : " + cause.toString());

		session.write("exceptionCaught");
		session.closeNow();
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("sessionCreated : " + session.getRemoteAddress().toString());
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("sessionOpened: " + session.getRemoteAddress().toString());
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		logger.info("messageReceived :" + String.valueOf(message));
		String requestCommand = String.valueOf(message);

		String responseCommand = commandService.executeCommand(requestCommand);
		
//	    String resMessage = String.format("%06d", result.getBytes().length) + result; // 전문길이(6)+명령어
		  
		  
		session.write(responseCommand); // 소켓응답

	}

    

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		logger.info("messageSent : " + message.toString());
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		if (status == IdleStatus.READER_IDLE) {
			logger.info("READER_IDLE");
			session.closeNow();
		} else if (status == IdleStatus.BOTH_IDLE) {
			logger.info("BOTH_IDLE");
			session.closeNow();
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("sessionClosed : " + session.getRemoteAddress().toString());
		int size = session.getService().getManagedSessions().values().size();
		logger.info("connected session " + size);
	}

}