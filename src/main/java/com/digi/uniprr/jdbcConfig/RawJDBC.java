package com.digi.uniprr.jdbcConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.digi.uniprr.VO.ImpactArticleDTO;

public class RawJDBC {

//    private static final String JDBC_URL = "jdbc:mysql://db-uniprr.c0jbkrha6hgp.us-west-2.rds.amazonaws.com/unitouch_medknow_live?serverTimezone=IST&useLegacyDatetimeCode=false&useSSL=false&useUnicode=yes&characterEncoding=UTF-8&characterSetResults=UTF-8";
//	private static final String JDBC_URL = "jdbc:mysql://db-uniprr.c0jbkrha6hgp.us-west-2.rds.amazonaws.com:3306/unitouch_medknow_live?useSSL=false";
//	private static final String JDBC_USER = "admin";
//    private static final String JDBC_PASS = "I89PyXhKQEvlY5Kepla2021";
	
//	//Live
	private static final String JDBC_URL = "jdbc:mysql://db-unitouch.c0jbkrha6hgp.us-west-2.rds.amazonaws.com/unitouch_medknow_live?useUnicode=yes&characterEncoding=UTF-8&characterSetResults=UTF-8";
	private static final String JDBC_USER = "admin";
	private static final String JDBC_PASS = "RuqCeWVWnEdOaCQNyzHY";
    
    public static List<ImpactArticleDTO> fetchImpactData(String aid) {
        List<ImpactArticleDTO> resultList = new ArrayList<>();

        String query = " select a.aid,i.author_review_url,j.is_impact from article_details a join journals j on a.journal_id=j.journal_id join impact_message_logs i on i.article_id =a.article_id\r\n"
        		+ " where a.aid = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, aid);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ImpactArticleDTO dto = new ImpactArticleDTO();
                dto.setAid(rs.getString("aid"));
                dto.setAuthorReviewUrl(rs.getString("author_review_url"));
                dto.setIsImpact(rs.getBoolean("is_impact"));
                resultList.add(dto);
            }

            rs.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
    
    public static boolean isImpactJournalById(String journalId) {
        boolean isImpact = false;
        String query = "SELECT is_impact FROM journals WHERE journal_acronym = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, journalId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                isImpact = rs.getBoolean("is_impact");
            }

            rs.close();
            ps.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isImpact;
    }

}

