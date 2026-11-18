package com.digi.uniprr.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.PublisherVo;
import com.digi.uniprr.model.Publishers;

public interface PublisherService 
{
    public String createPublisher(Publishers publishers);
    
    public List<Publishers> getPublishersList();
    
    public String updatePublisherDetails(Publishers publishers); 
    
    public String deleteById(Integer id);
    
    public boolean isIdExist(Integer id);
    
    /*****************Publisher Setup On Admin******************/
     
    String publisherSetupOnAdmin( PublisherVo publisherVo,MultipartFile file,HttpServletRequest request);
    
}
