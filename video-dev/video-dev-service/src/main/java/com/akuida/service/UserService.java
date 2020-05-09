package com.akuida.service;

import com.akuida.pojo.Users;

public interface UserService {
	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 * @return
	 */
	public boolean queryUsernameIsExist(String username);

	/**
	 * 保存用户信息
	 * 
	 * @param user
	 */
	public void saveUser(Users user);

	/**
	 * 查询密码是否一致
	 */
	public Users queryPasswordMacth(Users user);

	/**
	 * 修改用户信息
	 */
	public void updateUserInfo(Users user);

	/**
	 * 查询用户信息
	 * 
	 * @return
	 */
	public Users queryUserInfo(String userId);

	/**
	 * 查询用户是否喜欢点赞视频
	 * 
	 * @param userId
	 * @param videoId
	 * @return
	 */
	public Boolean isUserLikeVideo(String userId, String videoId);
}
