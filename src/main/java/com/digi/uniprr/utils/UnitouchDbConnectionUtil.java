package com.digi.uniprr.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

public class UnitouchDbConnectionUtil {


    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

//    @Value("${unitouch.db.url}")
//    private String unitouchDBurl;
//
//    @Value("${unitouch.db.username}")
//    private String unitouchDBUser;
//
//    @Value("${unitouch.db.password}")
//    private String unitouchDBPassword;


    private String unitouchDBurl="jdbc:mysql://db-uniprr.c0jbkrha6hgp.us-west-2.rds.amazonaws.com:3306/unitouch_medknow_live?zeroDateTimeBehavior=convertToNull&useSSL=false&useUnicode=yes&characterEncoding=UTF-8&characterSetResults=UTF-8";
    private String unitouchDBUser="admin";
    private String unitouchDBPassword="I89PyXhKQEvlY5Kepla2021";


    public Integer getJournalManuscriptCountFromUnitouch(String journalCode, Integer journalManuscriptCountFromUniprr) {

        int maxManuscriptCount=0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(unitouchDBurl, unitouchDBUser, unitouchDBPassword);

            Statement stmt = con.createStatement();
            Statement stmtForJournal=con.createStatement();

            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY,5);
            calendar.add(Calendar.MINUTE,30);
            int lastTwoDigits = calendar.get(Calendar.YEAR) % 100;

            String queryForJournal="select * from journals where LOWER(journal_abbr_name) ='"+journalCode+"'";

            ResultSet resultSetForJournal=stmtForJournal.executeQuery(queryForJournal);

            int journalId=0;

            while (resultSetForJournal.next())
            {
                journalId=resultSetForJournal.getInt("journal_id");
            }

            String query = "SELECT * FROM article_details where journal_id="+journalId+" and aid is not null and aid!='' and aid like '%"+lastTwoDigits+"' ORDER BY CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(aid, '_', 2), '_', -1) AS UNSIGNED) DESC LIMIT 1";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {

                String aid=rs.getString("aid");
                int journalManuscriptCountFromUnitouch=Integer.parseInt(aid.substring(aid.indexOf("_")+1, aid.lastIndexOf("_")));

                LOGGER.info("Last manuscript count of unitouch for journal id "+journalId+" is"+journalManuscriptCountFromUnitouch);
                LOGGER.info("Last manuscript count of uniprr is"+journalManuscriptCountFromUniprr);

                if(journalManuscriptCountFromUniprr>journalManuscriptCountFromUnitouch)
                {
                    maxManuscriptCount=journalManuscriptCountFromUniprr;
                }
                else
                {
                    maxManuscriptCount=journalManuscriptCountFromUnitouch;
                }

            }
            con.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return maxManuscriptCount;

    }



}
