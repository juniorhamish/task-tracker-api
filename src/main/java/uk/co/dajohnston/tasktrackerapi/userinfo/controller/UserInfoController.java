package uk.co.dajohnston.tasktrackerapi.userinfo.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.dajohnston.tasktrackerapi.userinfo.model.PartialUserInfo;
import uk.co.dajohnston.tasktrackerapi.userinfo.model.UserInfo;
import uk.co.dajohnston.tasktrackerapi.userinfo.service.UserInfoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userinfo")
@Slf4j
@Tag(name = "User Info")
public class UserInfoController {

  private final UserInfoService userInfoService;

  @GetMapping
  public UserInfo get(JwtAuthenticationToken authentication) {
    String userId = authentication.getToken().getSubject();
    log.info("Get user info for user '{}'", userId);
    return userInfoService.getUserInfo(userId);
  }

  @PatchMapping
  public UserInfo update(
      JwtAuthenticationToken authentication, @RequestBody PartialUserInfo userInfo) {
    String userId = authentication.getToken().getSubject();
    log.info("Patching user info for user '{}'", userId);
    return userInfoService.updateUserInfo(userId, userInfo);
  }
}
