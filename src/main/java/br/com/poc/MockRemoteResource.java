package br.com.poc;

import org.springframework.stereotype.Component;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
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
//Define propriedades hystrix a nivel de classe, todas as propriedades podem ser sobre escritas a nivel de método
/*Configurações do circuit breaker
 * circuitBreaker.requestVolumeThreshold é o Numero minimo de requisições que tem que acontecer dentro do tempo definido em
 * metricsRollingStats.timeInMilliseconds para ativar o circuit breaker caso a porcentagem de erro seja maior do que
 * circuitBreaker.errorThresholdPercentage, caso seja o circuit breaker vai bloquear todas as chamadas ao recurso pelo
 * tempo definido em circuitBreaker.sleepWindowInMilliseconds, metricsRollingStats.numBuckets é o numero de vezes que
 * o hystrix vai consultar o status das chamadas dentro da janela metricsRollingStats.timeInMilliseconds.
 * 
 * Mais informações e valores default em propriedadesHystrix.png
**/
@DefaultProperties(commandProperties={@HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),  
									  @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
									  @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),
									  @HystrixProperty(name="metricsRollingStats.timeInMilliseconds", value="15000"),
									  @HystrixProperty(name="metricsRollingStats.numBuckets", value="5")})
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
	
	//Método com fallback.
	@HystrixCommand(fallbackMethod="getDataWithFallback")
	public String getSlowDataWithFallback() {
		return getSlowData();
	}
	
	/**
	 * O método de fallback deve estar na mesma classe e ter a mesma assinatura do método principal
	 * caso o método receba parametros o método de fallback vai receber os mesmos parametros que 
	 * a chamada original que falhou recebeu.
	 */
	public String getDataWithFallback() {
		return "fallback result";
	}
	
	/**
	 * Por padrão todos os método anotados com @HystrixCommand que não configuram uma thread poll
	 * entram na thread pool padrão que tem tamanho padrão de 10 threads, o que não segue o padrão
	 * bulkhead já que um serviço lento pode bloquear todos as threads do pool e derrubar a aplicação inteira,
	 * o exemplo abaixo mostra como configurar uma threadPool que só se aplica a um método, e como configura-la.
	*/
	@HystrixCommand(threadPoolKey = "myThreadPool1",//Identificador único da threadPool
					threadPoolProperties = {@HystrixProperty(name="coreSize", value="30"), //Quantidade de threads disponiveis
											@HystrixProperty(name="maxQueueSize", value="10")})//Quantidade de threads que podem ficar aguardando
																							   //até que uma thread do pool libere sem retornar erro.
	public String getDataWithBulkhead() {
		return "ha! i have my own thread pool suckers!";
	}

	private void sleep(Long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
