package com.app.userService.user.domain.model;

public enum UserAction {
  CREATED,
  UPDATED,
  DELETED,
  LOGGED,
  LOGGED_WITH_TOKEN,
  LOGOUT,
  LOCKED,
  UNLOCKED,
  ERROR_LOGIN,
  UPDATE_PASSWORD,
  GRANTED_ADMIN
}
