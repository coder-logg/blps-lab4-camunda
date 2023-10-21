package edu.itmo.blps.camunda.delegate;

import edu.itmo.blps.model.category.Category;
import edu.itmo.blps.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.Spin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
@Slf4j
public class DeviceCategoriesLoadDelegate implements JavaDelegate {

	@Autowired
	private CategoryService categoryService;

	@AllArgsConstructor
	@Data
	static class Item implements Serializable {
		private String label;
		private String value;
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Map<String, String> productTypes = categoryService.getNamesAndIds(0, 100);
		List<Category> categories = categoryService.getAll(0, 100);
		List<Item> items = new ArrayList<>();
		productTypes.forEach((k,v)-> items.add(new Item(k, v)));
		execution.setVariable("categories", Spin.JSON(items));
		log.info("llll{}", execution.getVariable("categories"));
	}
}
