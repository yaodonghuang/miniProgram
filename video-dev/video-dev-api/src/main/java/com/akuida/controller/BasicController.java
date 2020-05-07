package com.akuida.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.akuida.utils.RedisOperator;

@RestController
public class BasicController {
	@Autowired
	public RedisOperator redis;
	
	public static final String USER_REDIS_SESSION = "user_redis_session";
	
	// 文件保存空间
	public static final String FILE_SPACE = "E:/uploadFace";
	
	// ffmpeg所在目录
	public static final String FFMPEG_EXE = "E:\\ffmpeg\\bin\\ffmpeg.exe";
	
	// pageSize(每页显示多少条)
	public static final Integer PAGE_SIZE = 5;
}
