package com.autumn.gateway.bo;

import lombok.Data;
import lombok.ToString;

/**
 * @program: dataearth-cloud-dev
 * @description:
 * @author: qius
 * @create: 2019-09-10:15:58
 */
@Data
@ToString
public class SignInfo {

  private UserInfo userInfo;

  private String sk;
}
