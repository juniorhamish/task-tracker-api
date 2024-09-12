package uk.co.dajohnston.tasktrackerapi.userinfo.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.co.dajohnston.tasktrackerapi.userinfo.model.UserInfoDTO;
import uk.co.dajohnston.tasktrackerapi.userinfo.service.UserInfoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userinfo")
@Slf4j
public class UserInfoController {

  private final UserInfoService userInfoService;

  @GetMapping
  public UserInfoDTO getUserInfo(JwtAuthenticationToken authentication) {
    String userId = authentication.getToken().getSubject();
    log.info("Get user info for user '{}'", userId);
    return userInfoService.getUserInfo(userId);
  }

  @PatchMapping
  public UserInfoDTO updateUserInfo(
      JwtAuthenticationToken authentication, @RequestBody UserInfoDTO userInfoDTO) {
    String userId = authentication.getToken().getSubject();
    log.info("Patching user info {} for user '{}'", userInfoDTO, userId);
    if (userInfoDTO.email() != null) {
      log.error("User '{}' attempted to change email address.", userId);
      throw new ResponseStatusException(BAD_REQUEST, "Email is read only");
    }
    return userInfoService.updateUserInfo(userId, userInfoDTO);
  }
}
