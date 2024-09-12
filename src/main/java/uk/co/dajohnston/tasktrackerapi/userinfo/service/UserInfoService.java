package uk.co.dajohnston.tasktrackerapi.userinfo.service;

import uk.co.dajohnston.tasktrackerapi.userinfo.model.UserInfoDTO;

public interface UserInfoService {

  UserInfoDTO getUserInfo(String id);

  UserInfoDTO updateUserInfo(String id, UserInfoDTO userInfoDTO);
}
