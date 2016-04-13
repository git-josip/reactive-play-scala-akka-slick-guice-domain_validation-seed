package com.josip.reactiveluxury.module.domain.user;

import com.josip.reactiveluxury.core.Asserts;

public enum UserStatus {
  VERIFIED(1L),
  NOT_VERIFIED(2L);
  UserStatus(Long id){
    this.id = id;
  }
  private final Long id;

  public Long getId() {
    return this.id;
  }

  public boolean isVerified() {
    return this == UserStatus.VERIFIED;
  }

  public static UserStatus of(Long idCandidate) {
    Asserts.argumentIsNotNull(idCandidate);

    if(UserStatus.VERIFIED.getId().equals(idCandidate))
      return UserStatus.VERIFIED;
    else if(UserStatus.NOT_VERIFIED.getId().equals(idCandidate))
      return UserStatus.NOT_VERIFIED;
    else
      throw new IllegalArgumentException(String.format("There is not UserStatus with provided id: '%s'", idCandidate));
  }
}
