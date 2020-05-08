package com.akuida.mapper;

import java.util.List;

import com.akuida.pojo.SearchRecords;
import com.akuida.utils.MyMapper;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

	public List<String> getHotWords();
}