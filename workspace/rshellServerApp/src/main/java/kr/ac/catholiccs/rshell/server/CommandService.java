package kr.ac.catholiccs.rshell.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.tools.javac.util.StringUtils;

import kr.ac.catholiccs.collections.FifoFixedSizeQueue;

@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CommandService {
	
	FifoFixedSizeQueue<String> queue = new FifoFixedSizeQueue<>(10);
	
	public void add(String command) {
		queue.add(command);
	}
	

	/**
	 * 
	 * @return 이전 수행한 명령어 목록을 반환한다.
	 */
	public String getHistory() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = queue.iterator();
		int seq = 1;
		
		while( it.hasNext() ) {
			String historyCommamd = String.format("%s. %s\r", seq, it.next());
			seq = seq + 1;
			sb.append(historyCommamd );
		}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param num  이전 수행한 명령순번
	 * @return 명령순번에 해다하는 명령어 반환
	 */
    private String getHistory(int num) {
    	String ret = "";
		Iterator<String> it = queue.iterator();
		int seq = 1;
		
		while( it.hasNext() ) {
			ret =it.next();
			if ( seq == num) break;
			seq = seq + 1;
		}
		return ret;
	}

	
	public String executeCommand(String command) throws InterruptedException, ExecutionException {
		String result = "";
		int num;
       
        
        if ("h".equals(command)) { 
        	return getHistory();
        } else if (command.startsWith("h ")) { // h and 공백의 경우
        	command = command.replace("h ", "");
        	command = command.replace(" ", "");
        	
        	try {
				num = Integer.parseInt(command);
			} catch (NumberFormatException e) {
				return "Usages : h <number>";
			}
        	
        	command = getHistory(num);
        	
        }
        
        add(command);
        
     // 운영체제 확인
        String os = System.getProperty("os.name").toLowerCase();
        
        
        if (os.contains("win")) {
        	result = executeCommandOnWindows(command);
        } else { // linux
        	result = executeCommandOnLinux(command);
        }
        

        return result;
	}
	
	

	// Windows에서 명령어 실행
    private String executeCommandOnWindows(String command) {
        return executeCommand(new String[]{"cmd", "/c", command});
    }

    // Linux에서 명령어 실행
    private String  executeCommandOnLinux(String command) {
        return executeCommand(new String[]{"/bin/bash", "-c", command});
    }

    private String executeCommand(String[] command) {
        StringBuilder result = new StringBuilder();
        try {
            
            ProcessBuilder processBuilder = new ProcessBuilder(command);
//            processBuilder.redirectErrorStream(true); // 에러 스트림을 표준 출력 스트림으로 합침
//            Map<String, String> environment = processBuilder.environment();
//            environment.put("LANG", "ko_KR.UTF-8");
            Process process = processBuilder.start();
            

            // 명령어 실행 결과를 읽어오기 위해 InputStream 사용
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "EUC-KR");
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // 명령어 실행 결과를 읽어와서 StringBuilder에 추가
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            // 프로세스 종료까지 대기
            int exitCode = process.waitFor();

            // 프로세스 종료 코드 출력
            System.out.println("Exit Code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // EUC-KR에서 UTF-8로 변환하여 결과 반환
        return result.toString();
    }

}
