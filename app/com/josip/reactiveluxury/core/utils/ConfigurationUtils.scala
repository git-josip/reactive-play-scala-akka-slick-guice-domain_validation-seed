package com.josip.reactiveluxury.core.utils

import com.josip.reactiveluxury.core.Asserts
import play.api.Play._

object ConfigurationUtils {
  private final val KEY_IS_NOT_APP_CONF_ERROR_FORMATTER = "%s not specified in application.conf"
  private final val TYPE_NOT_SUPPORTED_ERROR_FORMATTER = "%s type is not supported"

  def getConfigurationByKey[T](key: String)(implicit classManifest: Manifest[T]): T = {
    Asserts.argumentIsNotNullNorEmpty(key)

    classManifest match {
      case m if m == manifest[String] => current.configuration.getString(key).getOrElse(assureKeyState(key)).asInstanceOf[T]
      case m if m == manifest[Int]    => current.configuration.getInt(key).getOrElse(assureKeyState(key)).asInstanceOf[T]
      case m if m == manifest[Long]   => current.configuration.getLong(key).getOrElse(assureKeyState(key)).asInstanceOf[T]
      case _ => throw new RuntimeException(TYPE_NOT_SUPPORTED_ERROR_FORMATTER.format(classManifest.runtimeClass.getSimpleName))
    }
  }

  private def assureKeyState(key: String) {
    throw new IllegalStateException(KEY_IS_NOT_APP_CONF_ERROR_FORMATTER.format(key))
  }
}