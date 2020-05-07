package com.akuida.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akuida.service.BgmService;
import com.akuida.utils.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "背景音乐业务接口", tags = { "背景音乐相关的controller" })
@RequestMapping("/bgm")
public class BgmController {
	@Autowired
	private BgmService bgmService;

	@ApiOperation(value = "获取背景音乐列表", notes = "获取背景音乐列表接口")
	@PostMapping("/list")
	public JSONResult list() {
		return JSONResult.ok(bgmService.queryBgmList());
	}

}
