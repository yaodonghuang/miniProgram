package com.akuida.serviceImpl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.akuida.mapper.UsersFansMapper;
import com.akuida.mapper.UsersLikeVideosMapper;
import com.akuida.mapper.UsersMapper;
import com.akuida.mapper.UsersReportMapper;
import com.akuida.pojo.Users;
import com.akuida.pojo.UsersFans;
import com.akuida.pojo.UsersLikeVideos;
import com.akuida.pojo.UsersReport;
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
	private UsersFansMapper userFansMapper;
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	@Autowired
	private UsersReportMapper usersReportMapper;
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

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Boolean isUserLikeVideo(String userId, String videoId) {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
			return false;
		}
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveUserFanRelation(String userId, String fanId) {
		String relId = sid.nextShort();
		UsersFans userFan = new UsersFans();
		userFan.setId(relId);
		userFan.setFanId(fanId);
		userFan.setUserId(userId);
		userFansMapper.insert(userFan);
		userMapper.addFansCount(userId);
		userMapper.addFollersCount(fanId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteUserFanRelation(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);
		userFansMapper.deleteByExample(example);
		userMapper.reduceFansCount(userId);
		userMapper.reduceFollersCount(fanId);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean ifFollow(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);
		List<UsersFans> fansList = userFansMapper.selectByExample(example);
		if (fansList != null && fansList.size() > 0) {
			return true;
		}
		return false;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void reportUser(UsersReport usersReport) {
		String urId = sid.nextShort();
		usersReport.setId(urId);
		usersReport.setCreateDate(new Date());
		usersReportMapper.insert(usersReport);
	}

}
