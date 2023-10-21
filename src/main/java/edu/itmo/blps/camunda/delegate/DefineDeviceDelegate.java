package edu.itmo.blps.camunda.delegate;

import edu.itmo.blps.JwtUtils;
import edu.itmo.blps.model.category.Category;
import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.device.Device;
import edu.itmo.blps.service.DeviceService;
import edu.itmo.blps.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefineDeviceDelegate implements JavaDelegate {
	@Autowired
	private DeviceService deviceService;

	@Autowired
	private UserService userService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Request for registration: variables={}", execution.getVariables());
		String token = (String) execution.getVariable(HttpHeaders.AUTHORIZATION);
		if (!JwtUtils.validateAccessToken(token))
			throw new BpmnError("jwtCorrupted");
		Device newDevice = Device.builder()
				.name((String) execution.getVariable("name"))
				.price((Integer) execution.getVariable("price"))
				.description((String) execution.getVariable("description"))
				.category(new Category(Integer.parseInt((String) execution.getVariable("category"))))
				.company((Company) userService.loadUserByUsername(JwtUtils.parseToken(token).getSubject()))
				.build();
		deviceService.saveDevice(newDevice);
	}
}
