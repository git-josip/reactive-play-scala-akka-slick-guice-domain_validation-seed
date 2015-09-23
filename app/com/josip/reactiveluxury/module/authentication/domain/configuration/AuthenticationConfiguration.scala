package com.josip.reactiveluxury.module.authentication.domain.configuration

import com.josip.reactiveluxury.core.Asserts

abstract class AuthenticationConfiguration(
  final val secret            : String,
  final val tokenHoursToLive  : Int
) {
  Asserts.argumentIsNotNullNorEmpty(secret)
  Asserts.argumentIsNotNull(tokenHoursToLive)
  Asserts.argumentIsTrue(tokenHoursToLive > 0)
}
