package com.digi.uniprr.utils;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.uniprr.VO.ManuscriptListVO;
import com.jcraft.jsch.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SftpUtil {
	private final static Logger logger = LoggerFactory.getLogger(SftpUtil.class);

	private SftpUtil() {
	}

	public static ChannelSftp createSFTPChannel(String host, int port, String user, String password) {
		JSch jsch = new JSch();
		try {
			Session session = jsch.getSession(user, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			logger.info("------------ Sftp connected---------------");
			return (ChannelSftp) channel;
		} catch (JSchException e) {
			logger.error("Could not connect to SFTP : ", e);
		}
		return null;
	}

	public static void closeSftpConnection(ChannelSftp sftpChannel) {
		if (sftpChannel != null) {
			try {
				Session session = sftpChannel.getSession();
				sftpChannel.exit();
				session.disconnect();
			} catch (JSchException e) {
				logger.error("Failed to close session : ", e.getMessage());
			}
		}
	}

	public static boolean uploadToSftp(ManuscriptListVO manuscriptListVO, String baseFolderPath, String sftpIp,
			int sftpPort, String sftpUser, String sftpPassword, String sftpPath) {
		logger.info("---Inside uploadToSftp---");
		ChannelSftp sftpChannel = createSFTPChannel(sftpIp, sftpPort, sftpUser, sftpPassword);

		boolean success = true;
		if (sftpChannel != null) {
			logger.info("Copy file to sFTP server start");
			try {
				sftpChannel.put(baseFolderPath + manuscriptListVO.getManuscripCode() + "_UNIPRR.zip",
						sftpPath + File.separator + manuscriptListVO.getManuscripCode() + "_UNIPRR.zip");
				logger.info("Upload To sFTP Done");
			} catch (SftpException e) {
				success = false;
				logger.error("Exception while uploading to sFTP :" + e.getMessage(), e);
			}

			closeSftpConnection(sftpChannel);
		}

		return success;
	}

}
