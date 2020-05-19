package com.akuida.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.akuida.pojo.Videos;
import com.akuida.pojo.vo.VideosVo;
import com.akuida.utils.MyMapper;

public interface VideosMapperCustom extends MyMapper<Videos> {

	public List<VideosVo> queryAllVideos(@Param("videoDesc") String videoDesc, @Param("userId") String userId);

	public void addVideoLikeCount(String videoId);

	public void reduceVideoLikeCount(String videoId);

	public List<VideosVo> queryMyLikeVideos(String userId);

	public List<VideosVo> queryMyFollowVideos(String userId);
}