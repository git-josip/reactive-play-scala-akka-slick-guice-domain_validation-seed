package com.josip.reactiveluxury.configuration

import javax.inject.Singleton
import com.josip.reactiveluxury.core.utils.ConfigurationUtils
import com.josip.reactiveluxury.module.authentication.domain.configuration.AuthenticationConfiguration

@Singleton()
class AuthenticationConfigurationSetup extends AuthenticationConfiguration(
  secret            = ConfigurationUtils.getConfigurationByKey[String]("jwt.token.secret"),
  tokenHoursToLive  = ConfigurationUtils.getConfigurationByKey[Int]("jwt.token.hoursToLive")
)
