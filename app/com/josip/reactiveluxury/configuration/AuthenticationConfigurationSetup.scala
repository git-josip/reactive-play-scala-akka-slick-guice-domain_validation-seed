package com.josip.reactiveluxury.configuration

import javax.inject.{Inject, Singleton}

import com.josip.reactiveluxury.core.utils.ConfigurationUtils
import com.josip.reactiveluxury.module.domain.authentication.AuthenticationConfiguration

@Singleton()
class AuthenticationConfigurationSetup @Inject() (configurationUtils: ConfigurationUtils) extends AuthenticationConfiguration(
  secret            = configurationUtils.getConfigurationByKey[String]("jwt.token.secret"),
  tokenHoursToLive  = configurationUtils.getConfigurationByKey[Int]("jwt.token.hoursToLive")
)
