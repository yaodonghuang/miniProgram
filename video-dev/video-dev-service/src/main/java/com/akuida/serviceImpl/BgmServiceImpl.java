package com.akuida.serviceImpl;

import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.akuida.mapper.BgmMapper;
import com.akuida.pojo.Bgm;
import com.akuida.service.BgmService;

/**
 * Bgm实现类
 * 
 * @author Administrator
 *
 */
@Service
public class BgmServiceImpl implements BgmService {
	@Autowired
	private BgmMapper bgmMapper;
	@Autowired
	private Sid sid;

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<Bgm> queryBgmList() {
		return bgmMapper.selectAll();
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Bgm queryBgmById(String bgmId) {
		// TODO Auto-generated method stub
		return bgmMapper.selectByPrimaryKey(bgmId);
	}

}
