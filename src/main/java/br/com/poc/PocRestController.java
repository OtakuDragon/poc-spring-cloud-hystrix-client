package br.com.poc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class PocRestController {

	@Autowired
	private RestTemplate restTemplate;
	
	//Referencia poc poc-spring-cloud-eureka-client-2
	@RequestMapping(method=RequestMethod.GET)
    public String consumeService() {
		return restTemplate.getForEntity("http://poc-spring-cloud-eureka-client-2/api", String.class).getBody();
    }
	
}
