package uk.co.dajohnston.tasktrackerapi.userinfo.service;

import uk.co.dajohnston.tasktrackerapi.userinfo.model.UserInfo;

public interface UserInfoService {

  UserInfo getUserInfo(String id);

  UserInfo updateUserInfo(String id, UserInfo userInfo);
}
