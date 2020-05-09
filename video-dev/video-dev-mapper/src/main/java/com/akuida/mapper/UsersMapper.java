package com.akuida.mapper;

import com.akuida.pojo.Users;
import com.akuida.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {

	public void addReceiveLikeCount(String userId);
	
	public void reduceReceiveLikeCount(String userId);
}