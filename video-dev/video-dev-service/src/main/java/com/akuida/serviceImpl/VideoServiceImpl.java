package com.akuida.serviceImpl;

import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.akuida.mapper.SearchRecordsMapper;
import com.akuida.mapper.VideosMapper;
import com.akuida.mapper.VideosMapperCustom;
import com.akuida.pojo.SearchRecords;
import com.akuida.pojo.Videos;
import com.akuida.pojo.vo.VideosVo;
import com.akuida.service.VideoService;
import com.akuida.utils.PagedResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * Video实现类
 * 
 * @author Administrator
 *
 */
@Service
public class VideoServiceImpl implements VideoService {
	@Autowired
	private VideosMapper videoMapper;
	@Autowired
	private VideosMapperCustom videoMapperCustom;
	@Autowired
	private SearchRecordsMapper searchRecordsMapper;
	@Autowired
	private Sid sid;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String saveVideo(Videos video) {
		video.setId(sid.nextShort());
		videoMapper.insertSelective(video);
		return video.getId();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateVideo(String videoId, String coverPath) {
		Videos video = new Videos();
		video.setId(videoId);
		video.setCoverPath(coverPath);
		videoMapper.updateByPrimaryKeySelective(video);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {
		String desc = "";
		if (video != null) {
			desc = video.getVideoDesc();
			if (isSaveRecord != null && isSaveRecord == 1) {
				SearchRecords record = new SearchRecords();
				record.setId(sid.nextShort());
				record.setContent(desc);
				searchRecordsMapper.insert(record);
			}
		}
		PageHelper.startPage(page, pageSize);
		List<VideosVo> list = videoMapperCustom.queryAllVideos(desc);
		PageInfo<VideosVo> pageInfo = new PageInfo<>(list);
		PagedResult pagedResult = new PagedResult();
		pagedResult.setPage(page);
		pagedResult.setTotal(pageInfo.getPages());
		pagedResult.setRows(list);
		pagedResult.setRecords(pageInfo.getTotal());
		return pagedResult;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<String> getHotWords() {
		return searchRecordsMapper.getHotWords();
	}

}
