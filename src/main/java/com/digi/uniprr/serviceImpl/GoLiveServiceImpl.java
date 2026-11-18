package com.digi.uniprr.serviceImpl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.GoLiveVo;
import com.digi.uniprr.model.GoLive;
import com.digi.uniprr.repository.GoLiveRepo;
import com.digi.uniprr.service.GoLiveService;

@Service
@Transactional
public class GoLiveServiceImpl implements GoLiveService {
	
	@Autowired
	GoLiveRepo goLiveRepo;
	
	@Override
	public GoLiveVo getGoLiveData() {
		
		GoLiveVo goLiveVo = new GoLiveVo();
		
		GoLive goLive = goLiveRepo.getGoLiveData();
		if(goLive != null)
		{
		goLiveVo.setLiveLongMessage(goLive.getLiveLongMessage());
		goLiveVo.setLiveShortMessage(goLive.getLiveShortMessage());
		goLiveVo.setLiveStatus(goLive.getLiveStatus());
		goLiveVo.setStartDownTime(goLive.getLiveDownTime());
		goLiveVo.setUpTime(goLive.getUpTime());
		}
		return goLiveVo;
	}

}
