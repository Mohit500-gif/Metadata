package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.model.Announcements;

public interface AnnouncementService {

	List<Announcements> getAnnouncements(Announcements announcementsDto);

	Announcements createAndUpdateAnnouncements(Announcements announcementsDto);

	void updateAnnouncementsForInActive(Announcements announcementsDto);

	void deleteAnnouncement(int id);

}
