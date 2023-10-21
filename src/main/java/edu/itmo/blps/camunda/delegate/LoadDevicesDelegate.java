package edu.itmo.blps.camunda.delegate;

import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.device.Device;
import edu.itmo.blps.service.DeviceService;
import edu.itmo.blps.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.Spin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import edu.itmo.blps.camunda.delegate.DeviceCategoriesLoadDelegate.Item;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoadDevicesDelegate implements JavaDelegate {

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private UserService userService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		useHtmlForm(execution, deviceService.getAllDevices(0, 100, "name"),
				userService.getAllCompanies(0, 100, "name"));
//		useEmbeddedForm(execution, deviceService.getAllDevices(0, 100, "name"),
//				userService.getAllCompanies(0, 100, "name"));

	}

	private void useHtmlForm(DelegateExecution execution, List<Device> devices, List<Company> companies){
		Map<Integer, String> companyMap = makeMap(companies, Company::getId, Company::getName);
		Map<Integer, String> deviceMap = makeMap(devices, Device::getId, Device::getName);
		Map<Integer, List<Integer>> catalog = devices.stream()
				.collect(Collectors.toMap(
						x -> x.getCompany().getId(),
						x -> List.of(x.getId()),
						(a, b) -> {
							ArrayList<Integer> list = new ArrayList<>(a);
							list.addAll(b);
							return list;
						}
				));
		execution.setVariable("devices", Spin.JSON(deviceMap));
		execution.setVariable("companies", Spin.JSON(companyMap));
		execution.setVariable("catalog", Spin.JSON(catalog));
	}

	private void useEmbeddedForm(DelegateExecution execution, List<Device> devices, List<Company> companies){
		execution.setVariable("devices", Spin.JSON(makeItemListFromMap(makeMap(devices, Device::getName, Device::getId))));
		execution.setVariable("companies", Spin.JSON(makeItemListFromMap(makeMap(companies, Company::getName, Company::getId))));
		execution.setVariable("catalog","aaaa");
	}

	public static <T, K, V> Map<K, V> makeMap(List<T> list, Function<T, K> makeKey, Function<T, V> makeVal){
		return list.stream().collect(Collectors.toMap(makeKey, makeVal));
	}

	public static <K, V> List<Item> makeItemListFromMap(Map<K, V> map){
		List<Item> itemList = new ArrayList<>();
		map.forEach((k, v) -> itemList.add(new Item(k.toString(), v.toString())));
		return itemList;
	}

}
