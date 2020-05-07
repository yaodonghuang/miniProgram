package com.akuida.serviceImpl;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.akuida.mapper.UsersMapper;
import com.akuida.pojo.Users;
import com.akuida.service.UserService;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * 用户实现类
 * 
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UsersMapper userMapper;
	@Autowired
	private Sid sid;

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean queryUsernameIsExist(String username) {
		Users user = new Users();
		user.setUsername(username);
		Users result = userMapper.selectOne(user);
		return result == null ? false : true;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveUser(Users user) {
		user.setId(sid.nextShort());
		userMapper.insert(user);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryPasswordMacth(Users user) {
		Users searchUser = new Users();
		searchUser.setUsername(user.getUsername());
		Users result = userMapper.selectOne(searchUser);
		if (result != null && result.getPassword().equalsIgnoreCase(user.getPassword())) {
			return result;
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateUserInfo(Users user) {
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("id", user.getId());
		userMapper.updateByExampleSelective(user, userExample);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserInfo(String userId) {
		Users user = userMapper.selectByPrimaryKey(userId);
		return user;
	}

}
