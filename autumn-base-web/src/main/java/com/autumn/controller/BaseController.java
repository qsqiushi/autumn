package com.autumn.controller;

import com.autumn.exception.BusinessException;
import com.autumn.model.SessionAttribute;
import com.autumn.model.SessionAttributeAware;
import com.autumn.model.UserInfo;
import com.autumn.model.enums.ResultCode;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * @program: dataearth-online-dev
 * @description: controller基类
 * @author: qiushi
 * @create: 2019-01-11 14:54
 */
public class BaseController extends SessionAttributeAware {

  protected UserInfo getCurrentUser() {

    SessionAttribute attr = getSessionAttribute();
    UserInfo userInfoBO = new UserInfo();
    userInfoBO.setUserId(attr.getUserId());
    userInfoBO.setLoginName(attr.getLoginName());
    userInfoBO.setUserName(attr.getUserName());
    userInfoBO.setMobile(attr.getMobile());
    userInfoBO.setSysFlag(attr.getSysFlag());
    return userInfoBO;
  }

  @Override
  public String getUserId() {
    return super.getUserId();
  }

  protected void bindingResult(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      List<ObjectError> errorList = bindingResult.getAllErrors();
      for (ObjectError error : errorList) {
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), error.getDefaultMessage());
      }
    }
  }
}
