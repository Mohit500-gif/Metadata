package com.digi.uniprr.VO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserDetailsVO {
    private String userName;
    private Integer userId;
    private String email;
    private Integer roleId;
}
