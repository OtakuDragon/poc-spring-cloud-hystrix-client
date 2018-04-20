package br.com.poc;

import org.springframework.stereotype.Component;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

/**
 * 
 * Simula uma classe que acessa um serviço remoto, 
 * seja um banco de dados através de um JPA repository
 * ou um outro microserviço através de uma chamada rest.
 * 
 * A anotação @HystrixCommand envolve cada chamada a o método
 * em uma thread do hystrix que controla o timeout da chamada e fallback
 * caso falhe, a anotação @HystrixCommand padrão (sem parâmetros) define
 * um timeout de 1000ms, nenhum fallback e todas as execuções na mesma
 * threadpool(sem bulkhead pattern).
 *
 */
@Component
public class MockRemoteResource {

	//Método com circuit-breaker padrão de 1000ms simulando uma chamada a um recurso externo e finalizando a chamada em 1ms, a chamada é finalizada com sucesso.
	@HystrixCommand
	public String getFastData() {
		sleep(1L);
		return "result";
	}

	
	//Método com circuit-breaker padrão de 1000ms simulando uma chamada a um recurso externo e finalizando a chamada em 1500ms, a chamada joga uma java.util.concurrent.TimeoutException.
	@HystrixCommand
	public String getSlowData() {
		sleep(1500L);
		return "result";
	}
	
	//Método com circuit-breaker e timeout customizado de 3000ms.
	@HystrixCommand(commandProperties={@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000")})
	public String getCustomTimeoutData() {
		sleep(1500L);
		return "result";
	}

	private void sleep(Long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
