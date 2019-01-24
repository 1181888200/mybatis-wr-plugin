package com.lwl;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import com.lwl.domain.User;
import com.lwl.service.IUserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisWrApplicationTests {

	@Autowired
	private IUserService userService;
	
	/**
	 * 获取数据，获取的数据是从从库里获取的
	 * @author lwl
	 * @create 2019年1月24日 下午1:13:46
	 */
	@Test
	public void findUser() {
		
		List<User> findUser = userService.findUser();
		if(!CollectionUtils.isEmpty(findUser)) {
			System.out.println();
			System.out.println();
			System.out.println("------------------------------------------");
			for (User user : findUser) {
				System.out.println(user.toString());
			}
			System.out.println("------------------------------------------");
			System.out.println();
			System.out.println();
		}
	}
	
	/**
	 * 添加数据，自动添加到主库
	 * @author lwl
	 * @create 2019年1月24日 下午1:14:08
	 */
	@Test
	public void insert() {
		User user = new User("主库222","女","女王大人2222");
		userService.insertUser(user);
	}

	@Test
	public void del() {
		userService.delUser(11);
	}
	
	@Test
	public void upd() {
		User user = new User("主库222","女","女王大人2222");
		user.setId(1);
		userService.updUser(user);
	}

}

