package uk.co.dajohnston.houseworkapi.userinfo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.dajohnston.houseworkapi.userinfo.model.UserInfoDTO;
import uk.co.dajohnston.houseworkapi.userinfo.service.UserInfoService;

@RestController
@RequiredArgsConstructor
public class UserInfoController {

  private final UserInfoService userInfoService;

  @GetMapping("/userinfo")
  public UserInfoDTO getUserInfo(JwtAuthenticationToken authentication) {
    return userInfoService.getUserInfo(authentication.getToken().getSubject());
  }
}
