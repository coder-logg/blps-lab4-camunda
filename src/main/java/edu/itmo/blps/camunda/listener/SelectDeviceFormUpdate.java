package edu.itmo.blps.camunda.listener;

import edu.itmo.blps.model.device.Device;
import edu.itmo.blps.model.device.DeviceRepository;
import edu.itmo.blps.service.DeviceService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SelectDeviceFormUpdate implements TaskListener {
	@Autowired
	private DeviceService deviceService;
	@Override
	public void notify(DelegateTask delegateTask) {
//		delegateTask.setVariable("deviceDescription", deviceService.getDevice());
//		delegateTask.
	}
}
