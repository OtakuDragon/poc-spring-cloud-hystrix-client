package br.com.poc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PocRestController {

	@Autowired
	private MockRemoteResource mockRestResource;
	
	
	@RequestMapping(method=RequestMethod.GET, value="{speed}")
    public String consumeService(@PathVariable(name="speed") String speed) {
		if(speed.equals("fast")){
			return mockRestResource.getFastData();
		}
		
		if(speed.equals("slow")){
			return mockRestResource.getSlowData();
		}
		
		if(speed.equals("custom")){
			return mockRestResource.getCustomTimeoutData();
		}
		
		return null;
    }

}
