package org.example.petstorespring;

import org.example.petstorespring.entity.Category;
import org.example.petstorespring.persistence.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PetStoreSpringApplicationTests {

	@Autowired
	private CategoryMapper categoryMapper;

	@Test
	void contextLoads() {
		Category category = categoryMapper.selectById("CATS");
		String test = category.getName();
		System.out.println(test);
	}

}
