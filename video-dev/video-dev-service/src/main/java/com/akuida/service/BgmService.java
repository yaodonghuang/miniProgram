package com.akuida.service;

import java.util.List;

import com.akuida.pojo.Bgm;

public interface BgmService {
	/**
	 * 查询Bgm列表
	 * 
	 * @return
	 */
	public List<Bgm> queryBgmList();

	/**
	 * 	根据bgmId查询bgm
	 * @param bgmId
	 * @return
	 */
	public Bgm queryBgmById(String bgmId);
}
