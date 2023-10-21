package edu.itmo.blps.service;

import edu.itmo.blps.model.category.Category;
import edu.itmo.blps.model.category.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional
//	@Secured("ADMIN")
	public Category save(Category category){
		return categoryRepository.save(category);
	}

	public List<Category> getAll(Integer pageNo, Integer pageSize){
		return new ArrayList<>(categoryRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by("name"))).getContent());
	}

	public Map<String, String> getNamesAndIds(Integer pageNo, Integer pageSize){
		return getAll(pageNo, pageSize).stream().collect(Collectors.toMap(Category::getName, (v) -> String.valueOf(v.getId())));
	}

//	public String getAllIdsAsJson(Integer pageNo, Integer pageSize) throws JsonProcessingException {
//		Map<String, Integer> allIds = getAllIds(pageNo, pageSize);
////		getAll(pageNo, pageSize).stream().collect(Collectors.toMap(Category::getName, Category::getId))
//		;
//		log.info("categories:  {}", new ObjectMapper().writeValueAsString(allIds));
//		return new ObjectMapper().writeValueAsString(allIds);
//	}
}
