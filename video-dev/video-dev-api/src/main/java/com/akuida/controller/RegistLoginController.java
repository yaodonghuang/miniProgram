package com.akuida.controller;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.akuida.pojo.Users;
import com.akuida.pojo.vo.UsersVo;
import com.akuida.service.UserService;
import com.akuida.utils.JSONResult;
import com.akuida.utils.MD5Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "用户注册登录的接口", tags = { "注册和登录的controller" })
public class RegistLoginController extends BasicController {
	@Autowired
	private UserService userService;

	@ApiOperation(value = "用户注册", notes = "用户注册的接口")
	@PostMapping("/regist")
	public JSONResult regist(@RequestBody Users user) throws Exception {
		// 1. 判断用户名和密码必须不为空
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			return JSONResult.errorMsg("用户名和密码不能为空");
		}
		// 2.判断用户名是否存在
		Boolean ifExists = userService.queryUsernameIsExist(user.getUsername());
		// 3.保存用戶，註冊信息
		if (!ifExists) {
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setFansCounts(0);
			user.setFollowCounts(0);
			user.setReceiveLikeCounts(0);
			userService.saveUser(user);
		} else {
			return JSONResult.errorMsg("用户名已经存在，请换一个再试");
		}
		UsersVo userVo = getUserResultInfo(user);
		return JSONResult.ok(userVo);
	}

	public UsersVo getUserResultInfo(Users userMode) {
		userMode.setPassword("");
		String uniqueToken = UUID.randomUUID().toString();
		redis.set(USER_REDIS_SESSION + ":" + userMode.getId(), uniqueToken, 7 * 24 * 60 * 60);
		UsersVo userVo = new UsersVo();
		BeanUtils.copyProperties(userMode, userVo);
		userVo.setUserToken(uniqueToken);
		return userVo;
	}

	@ApiOperation(value = "用户登录", notes = "用户登录的接口")
	@PostMapping("/login")
	public JSONResult login(@RequestBody Users user) throws Exception {
		// 1. 判断用户名和密码必须不为空
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			return JSONResult.errorMsg("用户名和密码不能为空");
		}
		// 2.判断用户名是否存在
		Boolean ifExists = userService.queryUsernameIsExist(user.getUsername());
		if (!ifExists) {
			// 不存在，提示用户不存在
			return JSONResult.errorMsg("用户不存在，请输入正确的用户名！");
		} else {
			// 验证密码是否正确
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			Users getUser = userService.queryPasswordMacth(user);
			if (getUser == null) {
				return JSONResult.errorMsg("密码不正确，请重新输入！");
			}
			UsersVo userVo = getUserResultInfo(getUser);
			return JSONResult.ok(userVo);
		}
	}

	@ApiOperation(value = "用户注销", notes = "用户注销的接口")
	@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
	@PostMapping("/logout")
	public JSONResult logout(String userId) {
		redis.del(USER_REDIS_SESSION + ":" + userId);
		return JSONResult.ok();
	}
}
