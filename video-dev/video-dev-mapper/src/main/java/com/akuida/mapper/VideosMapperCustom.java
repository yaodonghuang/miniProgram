package com.akuida.mapper;

import java.util.List;

import com.akuida.pojo.Videos;
import com.akuida.pojo.vo.VideosVo;
import com.akuida.utils.MyMapper;

public interface VideosMapperCustom extends MyMapper<Videos> {

	public List<VideosVo> queryAllVideos();
}