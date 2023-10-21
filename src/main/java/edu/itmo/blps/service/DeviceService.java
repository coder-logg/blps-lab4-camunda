package edu.itmo.blps.service;

import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.device.Device;
import edu.itmo.blps.model.device.DeviceRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@Service
public class DeviceService {
	@Autowired
	private DeviceRepository deviceRepository;

	public List<Device> getAllDevices(Integer pageNo, Integer pageSize, String sortBy) {
		return deviceRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy))).getContent();
	}

	public Device getDevice(Integer id) {
		return deviceRepository.getById(id);
	}

	public Device getDeviceOrThrow(Integer id) {
		return deviceRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Device with id = " + id + " was not found"));
	}

	public Device findDevice(Device device) {
		if (!Objects.isNull(device.getId()))
			return getDeviceOrThrow(device.getId());
		else if (ObjectUtils.allNotNull(device.getCompany(), device.getName()))
			return getDeviceOrThrow(device.getCompany(), device.getName());
		else
			throw new IllegalArgumentException("Entity can't be identify: " + device);
	}

	public Device getDeviceOrThrow(Company company, String deviceName) {
		return deviceRepository.findDeviceByNameAndCompany(deviceName, company)
				.orElseThrow(() -> new EntityNotFoundException("Device was not found: " + deviceName));
	}

	public List<Device> getAllByCompany(Company company) {
		return deviceRepository.findAllByCompany(company);
	}

	public List<Device> getAllByCompanyId(Integer companyId) {
		return deviceRepository.findAllByCompany_Id(companyId);
	}

	@Transactional
//	@Secured({"ROLE_ADMIN", "ROLE_COMPANY"})
	public Device saveDevice(Device device) {
		return deviceRepository.save(device);
	}
}
