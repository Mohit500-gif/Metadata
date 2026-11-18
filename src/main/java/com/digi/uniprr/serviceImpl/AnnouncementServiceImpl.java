package com.digi.uniprr.serviceImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.Announcements;
import com.digi.uniprr.repository.AnnouncementRepo;
import com.digi.uniprr.service.AnnouncementService;

@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AnnouncementRepo announcementRepo;

	@Override
	public List<Announcements> getAnnouncements(Announcements announcementsDto) {
		List<Announcements> list = announcementRepo.findByJournalAndRole(announcementsDto.getJournalId(), announcementsDto.getRoleId());
		return list;
	}

	@Override
	public Announcements createAndUpdateAnnouncements(Announcements announcementsDto) {
		Announcements announcementsResponse = null;
		if (announcementsDto != null && announcementsDto.getId() != null && announcementsDto.getId() > 0) {
			try {
				Optional<Announcements> announcementsObj = announcementRepo.findById(announcementsDto.getId());
				if (announcementsObj.isPresent()) {
					announcementsResponse = announcementsObj.get();
					announcementsResponse.setAnnoucementMsg(announcementsDto.getAnnoucementMsg());
					announcementsResponse.setRoleId(announcementsDto.getRoleId());
					announcementsResponse.setActive(announcementsDto.getActive());
					announcementsResponse.setValidFrom(announcementsDto.getValidFrom());
					announcementsResponse.setValidTo(announcementsDto.getValidTo());

					announcementRepo.save(announcementsResponse);
				}
			} catch (Exception e) {
				log.error("Error while updating announcement {}", e);
			}
		} else if (announcementsDto != null && announcementsDto.getId() == null) {
			try {
				announcementsDto.setCreated_at(new Date());
				announcementsResponse = announcementRepo.save(announcementsDto);
			} catch (Exception e) {
				log.error("Error while creating announcement {}", e);
			}
		}
		return announcementsResponse;
	}

	@Override
	public void updateAnnouncementsForInActive(Announcements announcementsDto) {
		try {
			if (announcementsDto != null && announcementsDto.getId() != null && announcementsDto.getId() > 0) {
				announcementRepo.inactivateAnnouncement(announcementsDto.getId());
			}
		} catch (Exception e) {
			log.error("Error while deactivating announcement {}", e);
		}

	}

	@Override
	public void deleteAnnouncement(int id) {
		try {
			announcementRepo.deleteById(id);
		} catch (Exception e) {
			log.error("Error while delete announcement {}", e);
		}

	}
}
