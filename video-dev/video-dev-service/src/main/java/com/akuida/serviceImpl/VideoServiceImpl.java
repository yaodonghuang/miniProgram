package com.akuida.serviceImpl;

import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.akuida.mapper.SearchRecordsMapper;
import com.akuida.mapper.UsersLikeVideosMapper;
import com.akuida.mapper.UsersMapper;
import com.akuida.mapper.VideosMapper;
import com.akuida.mapper.VideosMapperCustom;
import com.akuida.pojo.SearchRecords;
import com.akuida.pojo.UsersLikeVideos;
import com.akuida.pojo.Videos;
import com.akuida.pojo.vo.VideosVo;
import com.akuida.service.VideoService;
import com.akuida.utils.PagedResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

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
	private UsersMapper usersMapper;
	@Autowired
	private VideosMapperCustom videoMapperCustom;
	@Autowired
	private SearchRecordsMapper searchRecordsMapper;
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
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
		String userId = "";
		if (video != null) {
			desc = video.getVideoDesc();
			userId = video.getUserId();
			if (isSaveRecord != null && isSaveRecord == 1) {
				SearchRecords record = new SearchRecords();
				record.setId(sid.nextShort());
				record.setContent(desc);
				searchRecordsMapper.insert(record);
			}
		}
		PageHelper.startPage(page, pageSize);
		List<VideosVo> list = videoMapperCustom.queryAllVideos(desc, userId);
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

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userLikeVideo(String userId, String videoId, String videoCreateId) {
		// 保存用户和视频的喜欢点赞关联关系表
		String likeId = sid.nextShort();
		UsersLikeVideos ulv = new UsersLikeVideos();
		ulv.setId(likeId);
		ulv.setUserId(userId);
		ulv.setVideoId(videoId);
		usersLikeVideosMapper.insert(ulv);
		// 2.视频喜欢数量累加
		videoMapperCustom.addVideoLikeCount(videoId);
		// 3.用户受喜欢数量的累加
		usersMapper.addReceiveLikeCount(videoCreateId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userUnLikeVideo(String userId, String videoId, String videoCreateId) {
		// 1.删除用户和视频的喜欢点赞关联关系表
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		usersLikeVideosMapper.deleteByExample(example);
		// 2.视频喜欢数量累减
		videoMapperCustom.reduceVideoLikeCount(videoId);
		// 3.用户受喜欢数量的累减
		usersMapper.reduceReceiveLikeCount(videoCreateId);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<VideosVo> list = videoMapperCustom.queryMyLikeVideos(userId);
		PageInfo<VideosVo> pageList = new PageInfo<>(list);

		PagedResult pagedResult = new PagedResult();
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setRows(list);
		pagedResult.setPage(page);
		pagedResult.setRecords(pageList.getTotal());
		return pagedResult;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<VideosVo> list = videoMapperCustom.queryMyFollowVideos(userId);
		PageInfo<VideosVo> pageList = new PageInfo<>(list);

		PagedResult pagedResult = new PagedResult();
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setRows(list);
		pagedResult.setPage(page);
		pagedResult.setRecords(pageList.getTotal());
		return pagedResult;
	}

}
