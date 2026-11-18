package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuScriptFile;

@Repository
@Transactional
public interface ManuScriptFileRepo extends JpaRepository<ManuScriptFile, Integer> {

	@Query(value = "select * from manuscript_file where manuscript_file_id=:manuscriptFileId", nativeQuery = true)
	List<ManuScriptFile> getFileDetailsById(Integer manuscriptFileId);

	@Query(value="SELECT * from manuscript_file where manuscript_file_id=:id" , nativeQuery = true)
	ManuScriptFile getById(Integer id);

	@Query("select m from ManuScriptFile m where m.manuscriptFileId=:manuscriptFileId AND m.fileName=:fileName")
	ManuScriptFile getManuscriptFileDetilsById(Integer manuscriptFileId, String fileName);

	@Modifying
	@Query("update ManuScriptFile m set m.fileType=:fileType,m.legend=:legend,m.fileVersion=:fileVersion where m.manuscriptFileId=:manuscriptFileId")
	void submitManuscript(Integer manuscriptFileId, String fileType,String legend,Integer fileVersion);
	
	@Query("select mf from ManuScriptFile mf where mf.manuScriptId=:Id")
	List<ManuScriptFile> getManuscriptFileById(Integer Id);

	
	@Query("select m from ManuScriptFile m  where m.manuScriptId=:manuscriptId AND m.isActive=:active AND m.fileVersion=:manuscriptVersion ORDER BY m.fileOrder ASC")
	List<ManuScriptFile> getManuscriptByactive(Integer manuscriptId,Integer manuscriptVersion, Integer active);
	
	@Query("select m from ManuScriptFile m  where m.manuScriptId=:manuscriptId AND m.isActive=:active AND m.fileVersion=:manuscriptVersion and m.fileType not in ('PDF','REVIEWERPDF','MergedFile') ORDER BY m.fileOrder ASC")
	List<ManuScriptFile> getManuscriptByactiveCM(Integer manuscriptId,Integer manuscriptVersion, Integer active);
	
	@Query(value="select journal_id from  manuscript  where manuScript_id=:manuscriptId",nativeQuery = true)
	Integer getJorunalId(Integer manuscriptId);
	
	@Query("select m from ManuScriptFile m  where m.manuScriptId=:manuscriptId AND m.fileVersion=:manuscriptVersion ORDER BY m.fileOrder ASC")
	List<ManuScriptFile> getManuscriptByactive(Integer manuscriptId,Integer manuscriptVersion);
	
	@Query("select m from ManuScriptFile m  where m.manuScriptId=:manuscriptId AND NOT (m.fileType = 'FINALREVISION' AND m.fileStatus = 'FINAL REVISION') ORDER BY m.fileOrder")
	List<ManuScriptFile> getAllVersionFileManuscriptByactive(Integer manuscriptId);
	
	@Query(value = "select manuscript_id from manuscript_file where manuscript_file_id=:manuscriptFileId",nativeQuery = true)
	Integer getManuscriptId(Integer manuscriptFileId);
	
	@Query(value="select MAX(m.file_version) from manuscript_file m  where m.manuscript_id=:manuscriptId ORDER BY m.file_order", nativeQuery = true)
	Integer getMaxManuscriptFileVersion(Integer manuscriptId);
	
	@Query("select m from ManuScriptFile m where m.manuscriptFileId=:manuscriptFileId")
	ManuScriptFile downloadFileByManuScriptFileID(Integer manuscriptFileId);
	
	@Query("select m from ManuScriptFile m where m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType='MergedFile'")
	ManuScriptFile getManuScriptFileData(Integer manuScriptId,Integer fileVersion);
	
	@Query("select m from ManuScriptFile m where m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType='Technical Modification Editor To Author'")
	ManuScriptFile getManuScriptFileDataForTechnicalModification(Integer manuScriptId,Integer fileVersion);
	
	@Query("select m from ManuScriptFile m where m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType='Revision file uploded successfully'")
	ManuScriptFile getManuScriptFileDataForRevisionByEic(Integer manuScriptId,Integer fileVersion);
	
	@Query("select m from ManuScriptFile m where m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType='PDF'")
	ManuScriptFile getManuScriptFileDataPDf(Integer manuScriptId,Integer fileVersion);
	
	@Query("select m from ManuScriptFile m where m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType='REVIEWERPDF'")
	ManuScriptFile getManuScriptFileDataPDfForReviewer(Integer manuScriptId,Integer fileVersion);
	
	@Query("select m from ManuScriptFile m where m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType=:fileType AND m.isActive = 1")
	List<ManuScriptFile> getManuScriptFileDataPDfList(Integer manuScriptId,Integer fileVersion,String fileType);
	
	
	@Query("select m from ManuScriptFile m where m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType='Main Documents/Article File' AND m.isActive=:active")
	ManuScriptFile getManuScriptFile(Integer manuScriptId,Integer fileVersion, Integer active);
	
	@Query("select m from ManuScriptFile m where m.manuScriptId=:manuScriptId  AND m.fileVersion=:fileVersion AND m.fileType='First Page file/Cover Letter' AND m.isActive=:active")
	ManuScriptFile getManuScriptFirstPageFile(Integer manuScriptId,Integer fileVersion,Integer active);
	
	@Query("SELECT m FROM ManuScriptFile m where m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType='Copyright Form' AND m.isActive=:active")
	ManuScriptFile getCopyRightFile(Integer manuScriptId,Integer fileVersion,Integer active);
	
	@Query("SELECT m FROM ManuScriptFile m where m.manuScriptId=:manuId")
	List<ManuScriptFile> getManuScriptFilegetById(Integer manuId);

	@Query(value="SELECT * FROM manuscript_file m WHERE m.manuscript_id=:manuscriptId AND m.file_version=:manuscriptVersion AND m.is_active=1 ORDER BY m.file_order ASC",nativeQuery = true)
	List<ManuScriptFile> getDataByManuscriptIdAndVersion(Integer manuscriptId, Integer manuscriptVersion);
	
	/*
	 * @Query("SELECT m FROM ManuScriptFile M where m.manuScriptId=:manuScriptId AND m.fileType='Main Documents/Article File' AND m.fileVersion=:fileVersion"
	 * ) ManuScriptFile downloadManuscriptDocFile(Integer manuscriptId,Integer
	 * manuscriptVersion);
	 */
	
	@Query("SELECT m FROM ManuScriptFile m where m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.isActive=1 AND m.fileName=:fileName")
	ManuScriptFile uploadUniqueFileWithTheName(Integer manuScriptId,Integer fileVersion,String fileName);
	
	@Query("SELECT m FROM ManuScriptFile m WHERE m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.isActive=1 ORDER BY m.manuscriptFileId")
	List<ManuScriptFile> getAllTheManuscriptFileWithMnuscriptVerison(Integer manuScriptId,Integer fileVersion);
	
	@Query("SELECT m FROM ManuScriptFile m WHERE m.manuScriptId =:manuScriptId AND m.fileVersion = (SELECT MAX(m2.fileVersion) FROM ManuScriptFile m2 WHERE m2.manuScriptId =:manuScriptId ) AND m.isActive = 1 ORDER BY m.manuscriptFileId")
	List<ManuScriptFile> getAllTheManuscriptFileWithMnuscriptVerison(Integer manuScriptId);
	
	@Query("SELECT m FROM ManuScriptFile m WHERE m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType='Main Documents/Article File' ORDER BY m.fileNumber ")
	List<ManuScriptFile> getMainDocumentOfManuscriptFileWithMnuscriptVerison(Integer manuScriptId,Integer fileVersion);
	
	@Query("SELECT COUNT(m) FROM ManuScriptFile m WHERE m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType='Main Documents/Article File' ")
	long countMainDocumentByIdAndVerison(Integer manuScriptId,Integer fileVersion);
	
	@Query("SELECT m FROM ManuScriptFile m where m.manuScriptId=:manuscriptId AND m.fileVersion=:manuscriptVersion AND m.fileNumber=:fileNumber")
	List<ManuScriptFile> getDataByManuscriptIdAndVersionAndNumber(Integer manuscriptId, Integer manuscriptVersion, Integer fileNumber);

	@Query(value="SELECT m.file_path FROM manuscript_file m WHERE m.manuscript_id=:manuscriptId AND m.file_type=:fileType ORDER BY m.manuscript_file_id  DESC LIMIT 1",nativeQuery = true)
	String getManuScriptFilePath(Integer manuscriptId, String fileType);
	
	@Query("Select m FROM ManuScriptFile m where m.fileType=:fileType AND m.isActive=1")
	List<ManuScriptFile> getAllFiguresList(String fileType);
	
	@Query(value="SELECT count(*) from manuscript_file m where m.manuscript_id=:manuScriptI  AND m.file_version=:fileVersio AND m.file_name LIKE'%Manuscript%' AND m.file_type='Main Documents/Article File' AND m.is_active=1", nativeQuery = true)
	Integer getManuScriptFileCheck(Integer manuScriptI,Integer fileVersio);
	
	@Query(value="SELECT count(*) from manuscript_file m where m.manuscript_id=:manuScriptI  AND m.file_version=:fileVersio AND m.file_name LIKE'%FirstPage%' AND m.file_type='First Page file/Cover Letter' AND m.is_active=1",nativeQuery = true)
	Integer getManuScriptFirstPageFileCheck(Integer manuScriptI,Integer fileVersio);
	
	@Modifying
	@Query(value="UPDATE manuscript_file SET is_active=2 where manuscript_id=:manuScriptI AND file_version=:fileVersio AND is_active=1",nativeQuery = true)
	void deleteTypeEmpltyFile(Integer manuScriptI, Integer fileVersio);

	@Modifying
	@Query(value="UPDATE manuscript_file SET file_type=:fileTyp,legend=:legen,file_version=:fileVersio,file_order=:fileOrde, file_number=:fileNumbe,is_active=1 WHERE  manuscript_file_id=:manuScriptFileI",nativeQuery = true)
	void updateFiletype(String fileTyp, String legen, Integer fileVersio, Integer fileOrde,Integer fileNumbe, Integer manuScriptFileI );
	
	@Modifying
	@Query(value="UPDATE manuscript_file SET file_name=:fileNam,file_path=:filePat WHERE  manuscript_file_id=:manuScriptFileI",nativeQuery = true)
	void updateFilePath(String fileNam, String filePat, Integer manuScriptFileI );
	
	@Query(value= "SELECT * FROM manuscript_file m WHERE m.manuscript_id=:manuScriptId AND m.file_version=:fileVersion AND m.is_active=1 AND m.file_type='Main Documents/Article File' ORDER BY m.created_on DESC LIMIT 1",nativeQuery = true)
	List<ManuScriptFile> getMainDocumentOfManuscriptFileWithversionIthenticateLatestFile(Integer manuScriptId,Integer fileVersion);
	
	@Query("select m from ManuScriptFile m where m.manuScriptId=:manuScriptId AND m.fileVersion=:fileVersion AND m.fileType='ReviewerMergedFile'")
	ManuScriptFile getManuScriptFileDataReviewer(Integer manuScriptId,Integer fileVersion);

	@Query(value="SELECT * FROM manuscript_file m WHERE m.manuscript_id=:manuscriptId AND m.file_version=:fileVersion AND m.is_active=1 AND m.file_type IN('PDF','REVIEWERPDF','MergedFile');",nativeQuery = true)
	List <ManuScriptFile> bundleFileList(Integer manuscriptId, Integer fileVersion);
	
	@Modifying
	@Query(value="UPDATE manuscript_file SET is_active=0 WHERE  manuscript_file_id=:manuscriptFileId",nativeQuery = true)
	void bundleFileStatusUpdate(Integer manuscriptFileId);
	
	@Query("Select m FROM ManuScriptFile m where m.manuScriptId=:manuscriptId AND m.fileVersion=:manuscriptVersion AND m.fileType in(:fileTypes) AND m.isActive=1 AND m.fileOrder IS NOT NULL ORDER BY m.fileOrder")
	List<ManuScriptFile> getActiveManuscriptFiles(int manuscriptId, int manuscriptVersion, List<String> fileTypes);

//	@Query("select m from ManuScriptFile m where m.manuScriptId=:manuscriptId AND m.fileStatus =:status ORDER BY m.manuscriptFileId")
//	List<ManuScriptFile> getManuscriptFileByStatus(Integer manuscriptId, String status);

	@Query(value = "select * from manuscript_file m where m.manuscript_id=:manuscriptId AND m.file_status = :status order by manuscript_file_id desc",nativeQuery = true)
	List<ManuScriptFile> getManuscriptFileByStatus(Integer manuscriptId, String status);

	@Query(value="select * from manuscript_file where manuscript_id=:manuscriptId and file_version=:maxVersion",nativeQuery = true)
	List<ManuScriptFile> getDataByManuscriptIdAndVersionAll(Integer manuscriptId, int maxVersion);


}
