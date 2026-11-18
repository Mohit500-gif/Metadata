package com.digi.uniprr.VO;

import com.digi.uniprr.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDetailsListVO {
    private List<UserDetailsVO> editors;
    private List<UserDetailsVO> reviewers;
    private List<UserDetailsVO> authors;
}
