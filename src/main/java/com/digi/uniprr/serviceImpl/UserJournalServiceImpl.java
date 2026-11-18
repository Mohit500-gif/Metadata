package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.UserJournalVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.enums.Salutation;
import com.digi.uniprr.model.Role;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.UserJournalService;
import com.digi.uniprr.utils.UserTransformationalUtils;

@Service
@Transactional
public class UserJournalServiceImpl implements UserJournalService {

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	UserRepo userRepo;

	@Override
	public List<UserVO> getListById(UserJournalVO userJournalVO) {
		List<UserVO> voList = new ArrayList<UserVO>();
		if (userJournalVO.getJournalId() == null || userJournalVO.getJournalId() == -1) {

			for (Integer mStatus : userJournalVO.getRoleID()) {
				List<Tuple> userList = new ArrayList<>();
				userList = userRepo.getUserByRoleId(mStatus);
				if (userList != null) {
					voList = userList.stream()
							.map(t -> new UserVO(t.get(0, Integer.class), t.get(1, String.class), t.get(2, String.class),
									t.get(3, String.class),Enum.valueOf(Salutation.class,t.get(4, String.class) != null ? t.get(4, String.class) :"Prof") , t.get(5, String.class),
											t.get(6, String.class), t.get(7, String.class), t.get(8, String.class), t.get(9, String.class),
											t.get(10, String.class), t.get(11, String.class), t.get(12, String.class),
											t.get(13, String.class), t.get(14, String.class), t.get(15, String.class),
											t.get(16, String.class),t.get(17, String.class))).collect(Collectors.toList());
				}
			}

		} else if (userJournalVO.getJournalId() > 0) {
			for (Integer roleID : userJournalVO.getRoleID()) {

				List<Tuple> userList = new ArrayList<>();
				userList = userRepo.getUserJournalRoleList(userJournalVO.getJournalId(), roleID);
				if (userList != null) {
					voList = userList.stream()
							.map(t -> new UserVO(t.get(0, Integer.class), t.get(1, String.class), t.get(2, String.class),
									t.get(3, String.class),Enum.valueOf(Salutation.class,t.get(4, String.class) != null ? t.get(4, String.class) :"Prof") , t.get(5, String.class),
									t.get(6, String.class), t.get(7, String.class), t.get(8, String.class), t.get(9, String.class),
									t.get(10, String.class), t.get(11, String.class), t.get(12, String.class),
									t.get(13, String.class), t.get(14, String.class), t.get(15, String.class),
									t.get(16, String.class),t.get(17, String.class)))
							.collect(Collectors.toList());
				}
			}

		}

		return voList;	
	}

	public List<UserVO> getUserListForJournal(UserJournalVO userJournalVO) {
		List<UserVO> voList = new ArrayList<UserVO>();
		if (userJournalVO.getJournalId() == null || userJournalVO.getJournalId() == -1) {

			for (Integer mStatus : userJournalVO.getRoleID()) {
				List<Tuple> userList = new ArrayList<>();
				userList = userRepo.getUserListByRoleId(mStatus);
				if (userList != null) {
					voList = userList.stream()
							.map(t -> new UserVO(t.get(0, Integer.class), t.get(1, String.class),
									t.get(2, String.class), t.get(3, String.class), t.get(4, String.class),
									t.get(5, String.class), t.get(6, String.class), t.get(7, Integer.class) ))
							.collect(Collectors.toList());
				}

			}

		} else if (userJournalVO.getJournalId() > 0) {
			for (Integer roleID : userJournalVO.getRoleID()) {

				List<Tuple> userList = new ArrayList<>();
				userList = userRepo.getUserList(userJournalVO.getJournalId(), roleID);
				if (userList != null) {
					voList = userList.stream()
							.map(t -> new UserVO(t.get(0, Integer.class), t.get(1, String.class),
									t.get(2, String.class), t.get(3, String.class), t.get(4, String.class),
									t.get(5, String.class), t.get(6, String.class), t.get(7, Integer.class)))
							.collect(Collectors.toList());
				}
			}

		}
		return voList;
	}

	@Override
	public UserJournal getUserJournalByJournal(Integer userId, Integer journalId) {
		return userJournalRepo.getUserJournalByJournal(userId, journalId);

	}

	@Override
	public String saveUserJournal(UserJournal userJournal) {
		userJournalRepo.save(userJournal);
		return "User journal save";
	}

	@Override
	public String activateUserJournal(UserJournalVO userJournalVO) {
		UserJournal getUserJournalData = userJournalRepo.activateUserJournal(userJournalVO.getUserId(),
				userJournalVO.getJournalId());
		if (getUserJournalData != null) {
			getUserJournalData.setIsActive(1);
			userJournalRepo.save(getUserJournalData);
			return "User journal has been activated.";
		}
		return "User journal link has been expired. ";
	}

	@Override
	public List<UserVO> getAssignedEditorListById(UserJournalVO userJournalVO) {
		List<UserVO> voList = new ArrayList<UserVO>();
		if (userJournalVO.getJournalId() > 0) {

			List<User> userList = userRepo.getAssignedEditorListById(userJournalVO.getJournalId(),
					userJournalVO.getManuscriptId());
			for (User user : userList) {
				voList.add(UserTransformationalUtils.mapUserModelToUserVO(user));
			}
		}

		return voList;

	}
}
