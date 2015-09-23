package com.josip.reactiveluxury.core

trait Converter[TIn, TOut]
{
  def convert(in :TIn): TOut
}
