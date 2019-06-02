package com.skuniv.cs.geonyeong.kaggleapi.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String id;
    private String password;
    private String displayName;
    private String aboutMe;
    private String email;
    private String age;
    private String createDate;
    private Integer upvotes;
    private Integer downVotes;
    private String profileImageUrl;
    private String websiteUrl;
}
