package kr.ac.catholiccs.rshell.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class Client {
	
//	@Value("${remote.dev.ip}")
	private String ip;
	
//	@Value("${remote.dev.port}")
	private int port;
	
	public Client() {
		
	}
	
	public Client(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
    public String sendMessageToServer(String message) {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 서버에 메시지 송신
            out.println(message);

            String response = receiveMessage(in);
            
            // 서버에서의 응답 수신
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            return "오류 발생";
        }  
        
        finally {
            // 소켓 닫기
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    private String receiveMessage(BufferedReader in) throws IOException {
        // 첫 6자리를 읽어 전문의 길이를 가져옴
        char[] lengthChars = new char[6];
        if (in.read(lengthChars) != lengthChars.length) {
            throw new IOException("Failed to read message length");
        }

        int length = Integer.parseInt(new String(lengthChars));

        // 남은 데이터가 전문의 길이보다 짧으면 더 이상 처리하지 않음
        StringBuilder messageBuilder = new StringBuilder();
        char[] buffer = new char[1024];
        int bytesRead;

        while (messageBuilder.length() < length && (bytesRead = in.read(buffer)) != -1) {
            messageBuilder.append(buffer, 0, bytesRead);
        }

        return messageBuilder.toString();
    }

}
