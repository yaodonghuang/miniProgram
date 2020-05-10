package com.akuida.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.akuida.pojo.Users;
import com.akuida.pojo.vo.PublisherVideo;
import com.akuida.pojo.vo.UsersVo;
import com.akuida.service.UserService;
import com.akuida.utils.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "用户相关业务接口", tags = { "用户相关的controller" })
@RequestMapping("/user")
public class UserController extends BasicController {
	@Autowired
	private UserService userService;

	@ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
	@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
	@PostMapping("/uploadFace")
	public JSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws Exception {
		if (StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("用户id不能为空！");
		}
		// 文件命名空间
		String fileSpace = "E:/uploadFace";
		// 保存到数据库中的相对路径
		String uploadPathDb = "/" + userId + "/face";
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if (files != null && files.length > 0) {
				String fileName = files[0].getOriginalFilename();
				if (StringUtils.isNoneBlank(fileName)) {
					// 文件上传的最终保存路径
					String finalPath = fileSpace + uploadPathDb + "/" + fileName;
					// 设置数据库保存的路径
					uploadPathDb += ("/" + fileName);

					File file = new File(finalPath);
					if (file.getParentFile() != null || !file.getParentFile().isDirectory()) {
						file.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(file);
					inputStream = files[0].getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} else {
				return JSONResult.errorMsg("上传出错！");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}
		Users user = new Users();
		user.setId(userId);
		user.setFaceImage(uploadPathDb);
		userService.updateUserInfo(user);
		return JSONResult.ok(uploadPathDb);
	}

	@ApiOperation(value = "用户信息查询", notes = "用户信息查询接口")
	@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
	@PostMapping("/query")
	public JSONResult query(String userId) throws Exception {
		if (StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("用户id不能为空！");
		}
		Users user = userService.queryUserInfo(userId);
		UsersVo usersVo = new UsersVo();
		BeanUtils.copyProperties(user, usersVo);
		return JSONResult.ok(usersVo);
	}

	@PostMapping("/queryPublisher")
	public JSONResult queryPublisher(String loginUserId, String videoId, String publisherUserId) throws Exception {
		if (StringUtils.isBlank(publisherUserId)) {
			return JSONResult.errorMsg("");
		}
		// 1.查询视频发布者的信息
		Users user = userService.queryUserInfo(publisherUserId);
		UsersVo publisher = new UsersVo();
		BeanUtils.copyProperties(user, publisher);
		// 2.查询当前登陆者和视频的点赞关系
		Boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);

		PublisherVideo bean = new PublisherVideo();
		bean.setPublisher(publisher);
		bean.setUserLikeVideo(userLikeVideo);
		return JSONResult.ok(bean);
	}

	@PostMapping("/beyourfans")
	public JSONResult beyourfans(String userId, String fanId) throws Exception {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
			return JSONResult.errorMsg("");
		}
		userService.saveUserFanRelation(userId, fanId);
		return JSONResult.ok("关注成功");
	}

	@PostMapping("/dontbeyourfans")
	public JSONResult dontbeyourfans(String userId, String fanId) throws Exception {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
			return JSONResult.errorMsg("");
		}
		userService.deleteUserFanRelation(userId, fanId);
		return JSONResult.ok("取消关注成功");
	}
}
