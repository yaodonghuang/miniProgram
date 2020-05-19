package com.akuida.service;

import java.util.List;

import com.akuida.pojo.Videos;
import com.akuida.utils.PagedResult;

public interface VideoService {
	/**
	 * 保存video
	 */
	public String saveVideo(Videos video);

	/**
	 * 修改视频封面
	 * 
	 * @return
	 */
	public void updateVideo(String videoId, String coverPath);

	/**
	 * 分页查询视频列表
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize);

	/**
	 * 获取热搜词
	 * 
	 * @return
	 */
	public List<String> getHotWords();

	/**
	 * 用户喜欢视频
	 * 
	 * @param userId
	 * @param videoId
	 * @param videoCreateId
	 */
	public void userLikeVideo(String userId, String videoId, String videoCreateId);

	/**
	 * 用户不喜欢视频
	 * 
	 * @param userId
	 * @param videoId
	 * @param videoCreateId
	 */
	public void userUnLikeVideo(String userId, String videoId, String videoCreateId);

	/**
	 * 我收藏点赞过的视频列表
	 * 
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

	/**
	 * 我关注的人发的视频
	 * 
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);
}
