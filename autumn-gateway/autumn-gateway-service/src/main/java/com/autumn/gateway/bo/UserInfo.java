package com.autumn.gateway.bo;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @program: airlook-dev
 * @description:
 * @author: qius
 * @create: 2020-12-24:16:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserInfo {

  private String userId;

  private String loginName;

  private String mobile;

  private String email;

  private String userName;

  private String userPicUrl;

  private Integer sysFlag;

  private List<String> roleCodes;
}
