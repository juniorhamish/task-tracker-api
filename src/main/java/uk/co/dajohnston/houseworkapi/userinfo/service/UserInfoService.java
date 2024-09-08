package uk.co.dajohnston.houseworkapi.userinfo.service;

import uk.co.dajohnston.houseworkapi.userinfo.model.UserInfoDTO;

public interface UserInfoService {

  UserInfoDTO getUserInfo(String id);

  UserInfoDTO updateUserInfo(String id, UserInfoDTO userInfoDTO);
}
