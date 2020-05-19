package com.akuida.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.akuida.enums.VideoStatusEnum;
import com.akuida.pojo.Bgm;
import com.akuida.pojo.Videos;
import com.akuida.service.BgmService;
import com.akuida.service.VideoService;
import com.akuida.utils.JSONResult;
import com.akuida.utils.MergeVideoMp3;
import com.akuida.utils.PagedResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "视频相关业务接口", tags = { "视频相关业务的controller" })
@RequestMapping("/video")
public class VideoController extends BasicController {
	@Autowired
	private BgmService bgmService;

	@Autowired
	private VideoService videoService;

	@ApiOperation(value = "上传视频", notes = "上传视频的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form") })
	@PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
	public JSONResult upload(String userId, String bgmId, double videoSeconds, int videoWidth, int videoHeight,
			String desc, @ApiParam(value = "短视频", required = true) MultipartFile videoFile) throws Exception {
		if (StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("用户id不能为空！");
		}
		// 保存到数据库中的相对路径
		String uploadPathDb = "/" + userId + "/video";
		String coverPathDb = "/" + userId + "/video";
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		String finalPath = "";
		try {
			if (videoFile != null) {
				String fileName = videoFile.getOriginalFilename();
				if (StringUtils.isNoneBlank(fileName)) {
					// 文件上传的最终保存路径
					finalPath = FILE_SPACE + uploadPathDb + "/" + fileName;
					// 设置数据库保存的路径
					uploadPathDb += ("/" + fileName);
					String[] nameArray = fileName.split("\\.");
					String fileNamePrefix = "";
					if (nameArray.length == 1) {
						fileNamePrefix = fileName.split("\\.")[0];
					} else {
						for (int i = 0; i < nameArray.length - 1; i++) {
							fileNamePrefix = fileNamePrefix + nameArray[i];
						}
					}
					coverPathDb = coverPathDb + "/" + fileNamePrefix + ".jpg";
					File file = new File(finalPath);
					if (file.getParentFile() != null || !file.getParentFile().isDirectory()) {
						file.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(file);
					inputStream = videoFile.getInputStream();
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
		// 判断是否存在bgmId，存在则查询BGM
		MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
		if (StringUtils.isNoneBlank(bgmId)) {
			Bgm bgm = bgmService.queryBgmById(bgmId);
			String mp3InputPath = FILE_SPACE + bgm.getPath();// 音乐相对路径
			uploadPathDb = "/" + userId + "/video/" + UUID.randomUUID().toString() + ".mp4";
			String outPath = FILE_SPACE + uploadPathDb;
			tool.convertor(finalPath, mp3InputPath, videoSeconds, outPath);
		}

		// 视频截图
		tool.getCover(finalPath, FILE_SPACE + coverPathDb);

		// 保存视频信息到数据库
		Videos video = new Videos();
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoSeconds((float) videoSeconds);
		video.setVideoHeight(videoHeight);
		video.setVideoWidth(videoWidth);
		video.setVideoDesc(desc);
		video.setVideoPath(uploadPathDb);
		video.setCreateTime(new Date());
		video.setCoverPath(coverPathDb);
		video.setStatus(VideoStatusEnum.SUCCESS.value);
		String videoId = videoService.saveVideo(video);
		return JSONResult.ok(videoId);
	}

	@ApiOperation(value = "上传封面", notes = "上传封面的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoId", value = "视频id", required = true, dataType = "String", paramType = "form") })
	@PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
	public JSONResult uploadCover(String videoId, String userId,
			@ApiParam(value = "视频封面", required = true) MultipartFile videoFile) throws Exception {
		if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("视频主键id和用户id不能为空！");
		}
		// 保存到数据库中的相对路径
		String uploadPathDb = "/" + userId + "/video";
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		String finalCoverPath = "";
		try {
			if (videoFile != null) {
				String fileName = videoFile.getOriginalFilename();
				if (StringUtils.isNoneBlank(fileName)) {
					// 文件上传的最终保存路径
					finalCoverPath = FILE_SPACE + uploadPathDb + "/" + fileName;
					// 设置数据库保存的路径
					uploadPathDb += ("/" + fileName);
					File file = new File(finalCoverPath);
					if (file.getParentFile() != null || !file.getParentFile().isDirectory()) {
						file.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(file);
					inputStream = videoFile.getInputStream();
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
		videoService.updateVideo(videoId, uploadPathDb);
		return JSONResult.ok();
	}

	/**
	 * 分页和搜索查询视频列表
	 * 
	 * @param video
	 * @param isSaveRecord
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@PostMapping(value = "/showAll")
	public JSONResult showAll(@RequestBody Videos video, Integer isSaveRecord, Integer page, Integer pageSize)
			throws Exception {
		page = page == null ? 1 : page;
		pageSize = pageSize == null ? PAGE_SIZE : pageSize;
		PagedResult pr = videoService.getAllVideos(video, isSaveRecord, page, pageSize);
		return JSONResult.ok(pr);
	}

	/**
	 * 我收藏过的视频列表
	 */
	@PostMapping(value = "/showMyLike")
	public JSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception{
		if (StringUtils.isBlank(userId)) {
			return JSONResult.ok();
		}
		page = page == null ? 1 : page;
		pageSize = pageSize == null ? 6 : pageSize;
		PagedResult videoList = videoService.queryMyLikeVideos(userId, page, pageSize);
		return JSONResult.ok(videoList);
	}
	
	/**
	 * 	我关注的人发的视频
	 */
	@PostMapping(value = "/showMyFollow")
	public JSONResult showMyFollow(String userId, Integer page, Integer pageSize) throws Exception{
		if (StringUtils.isBlank(userId)) {
			return JSONResult.ok();
		}
		page = page == null ? 1 : page;
		pageSize = pageSize == null ? 6 : pageSize;
		PagedResult videoList = videoService.queryMyFollowVideos(userId, page, pageSize);
		return JSONResult.ok(videoList);
	}
	
	/**
	 * 热搜词列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/hot")
	public JSONResult hot() throws Exception {
		return JSONResult.ok(videoService.getHotWords());
	}

	@PostMapping(value = "/userLike")
	public JSONResult userLike(String userId, String videoId, String videoCreateId) throws Exception {
		videoService.userLikeVideo(userId, videoId, videoCreateId);
		return JSONResult.ok();
	}

	@PostMapping(value = "/userUnLike")
	public JSONResult userUnLike(String userId, String videoId, String videoCreateId) throws Exception {
		videoService.userUnLikeVideo(userId, videoId, videoCreateId);
		return JSONResult.ok();
	}
}
