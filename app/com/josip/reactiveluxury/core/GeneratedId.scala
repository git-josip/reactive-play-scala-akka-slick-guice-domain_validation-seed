package com.josip.reactiveluxury.core

case class GeneratedId(id: Long)
{
  Asserts.argumentIsTrue(id > 0)
}
