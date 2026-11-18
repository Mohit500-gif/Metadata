package com.digi.uniprr.serviceImpl;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.PublisherVo;
import com.digi.uniprr.model.Publishers;
import com.digi.uniprr.repository.PublisherRepo;
import com.digi.uniprr.service.PublisherService;

@Service
@Transactional
public class PublisherServiceImpl implements PublisherService {

	@Autowired
	PublisherRepo publisherRepo;

	@Override
	public String createPublisher(Publishers publishers) {
		publisherRepo.save(publishers);
		return null;
	}

	@Override
	public boolean isIdExist(Integer id) {

		return publisherRepo.existsById(id);
	}

	@Override
	public List<Publishers> getPublishersList() {
		return publisherRepo.findAll();
	}

	@Override
	public String updatePublisherDetails(Publishers publishers) {
		publisherRepo.save(publishers);
		return "Updated";
	}

	@Override
	public String deleteById(Integer id) {
		publisherRepo.deletePublisherById(id);
		return "Deleted successfully";
	}

	@Override
	public String publisherSetupOnAdmin(PublisherVo publisherVo, MultipartFile file, HttpServletRequest request) {
		if (publisherVo.getPublisherId() != null) {
			Publishers updatePublishers = publisherRepo.getOne(publisherVo.getPublisherId());
			updatePublishers.setPublisherCode(publisherVo.getPublisherCode());
			updatePublishers.setPublisherContactEmailId(publisherVo.getPublisherContactEmailId());
			updatePublishers.setPublisherName(publisherVo.getPublisherName());
			updatePublishers.setPublishersAddress(publisherVo.getPublisherAddress());
			updatePublishers.setPublisherSite(publisherVo.getPublisherSite());
			updatePublishers.setPublisherType(publisherVo.getPublisherType());
			updatePublishers.setPublishingType(publisherVo.getPublishingType());
			updatePublishers.setUpdatedBy(publisherVo.getUpdatedBy());
			updatePublishers.setUpdatedOn(new Date());
			updatePublishers.setPublisherContactNumber(publisherVo.getPublisherContactNumber());
			if (!file.isEmpty()) {
				String location = System.getProperty("catalina.base");
				String attachmentFolderPath = location + File.separator + "webapps" + File.separator + "PublisherLogo";
				File dir = new File(attachmentFolderPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				try {
					byte[] bytes = file.getBytes();
					 Path path = Paths.get(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
					Files.write(path, bytes);
					Integer port = request.getServerPort();
					InetAddress inetAddress = InetAddress.getLocalHost();
					String Ip = "http://" + inetAddress.getHostAddress() + ":" + port;
					String fileServerPath = Ip + File.separator + "PublisherLogo" + File.separator
							+ file.getOriginalFilename();
					updatePublishers.setPublisherLogo(fileServerPath);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			publisherRepo.save(updatePublishers);
			return "Publisher Updated SucessFully";
		} else {
			Publishers createPublishers = new Publishers();
			createPublishers.setPublisherCode(publisherVo.getPublisherCode());
			createPublishers.setPublisherContactEmailId(publisherVo.getPublisherContactEmailId());
			createPublishers.setPublisherName(publisherVo.getPublisherName());
			createPublishers.setPublishersAddress(publisherVo.getPublisherAddress());
			createPublishers.setPublisherSite(publisherVo.getPublisherSite());
			createPublishers.setPublisherType(publisherVo.getPublisherType());
			createPublishers.setPublishingType(publisherVo.getPublishingType());
			createPublishers.setCreatedBy(publisherVo.getCreatedBy());
			createPublishers.setPublisherContactNumber(publisherVo.getPublisherContactNumber());
			if (!file.isEmpty()) {
				String location = System.getProperty("catalina.base");
				String attachmentFolderPath = location + File.separator + "webapps" + File.separator + "PublisherLogo";
				File dir = new File(attachmentFolderPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				try {
					byte[] bytes = file.getBytes();
					 Path path = Paths.get(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
					Files.write(path, bytes);
					Integer port = request.getServerPort();
					InetAddress inetAddress = InetAddress.getLocalHost();
					String Ip = "http://" + inetAddress.getHostAddress() + ":" + port;
					String fileServerPath = Ip + File.separator + "PublisherLogo" + File.separator
							+ file.getOriginalFilename();
					createPublishers.setPublisherLogo(fileServerPath);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}			publisherRepo.save(createPublishers);
			return "Publisher Created SucessFully";
		}
	}

}
