package kr.ac.catholiccs.rshell.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

/**
 * 각 서버에 명령을 넣으면 해당 서버에서 값을 받는다
 * 1. 본 클래스(CLI)가 생성되면 스프링부트는 @Value 를 찾아서 properties로 부터
 * 정보를 변수에 할당한다.
 * - 스프링부트는 Shellcomponent Annotation 을 컨테이너에 주입한다. 
 * - 본 클래스의 Value annotation에 의해 환경값(= /resource/application.properties) 으로 부터 목적서버의 정보를  변수에 할당한다. 
 * .
 * @author devOps
 *
 */

@ShellComponent
public class CLI {
	
	// ---------- 개발 ---------------------------
	@Value("${remote.dev.ip}")
	private String ipDev;
	
	@Value("${remote.dev.port}")
	private int portDev;

	// ---------- 테스트 ---------------------------
	@Value("${remote.test.ip}")
	private String ipTest;
	
	@Value("${remote.test.port}")
	private int portTest;
	
	// ---------- 운영 -----------------------------
	@Value("${remote.prod.ip}")
	private String ipProd;
	
	@Value("${remote.prod.port}")
	private int portProd;
	
	@ShellMethod("개발서버")
    public String dev(String... args) {
        
      for (String arg : args) {
         System.out.println(arg);
      }
      
      String userInputCommand = String.join(" ", args);
      
      String reqCommand = String.format("%06d", userInputCommand.getBytes().length) + userInputCommand; // 전문길이(6)+명령어
 
      Client client = new Client(ipDev, portDev );
      String resCommand = client.sendMessageToServer(reqCommand);
        
      return resCommand;
    }

	
	@ShellMethod("테스트서버")
    public String test(String... args) {
        
      for (String arg : args) {
         System.out.println(arg);
      }
      
      String userInputCommand = String.join(" ", args);
      
      String reqCommand = String.format("%06d", userInputCommand.getBytes().length) + userInputCommand; // 전문길이(6)+명령어
 
      Client client = new Client(ipTest, portTest );
      String resCommand = client.sendMessageToServer(reqCommand);
        
      return resCommand;
    }
	
	@ShellMethod("운영서버")
    public String prod(String... args) {
        
      for (String arg : args) {
         System.out.println(arg);
      }
      
      String userInputCommand = String.join(" ", args);
      
      String reqCommand = String.format("%06d", userInputCommand.getBytes().length) + userInputCommand; // 전문길이(6)+명령어
 
      Client client = new Client(ipProd, portProd );
      String resCommand = client.sendMessageToServer(reqCommand);
        
      return resCommand;
    }
}

