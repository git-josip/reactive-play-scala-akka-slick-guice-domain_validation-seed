package com.josip.reactiveluxury.core.slick.generic

import scala.language.experimental.macros

//:TODO: macros needs to be in separate build to be used
object EntityHelper {
  import scala.reflect.macros.blackbox

  def withNewProperty[T, I](entity: T, propertyValue: I): T = macro withNewPropertyImpl[T, I]

  def withNewPropertyImpl[T: c.WeakTypeTag, I: c.WeakTypeTag](c: blackbox.Context)(entity: c.Expr[T], propertyValue: c.Expr[I]): c.Expr[T] = {
    import c.universe._

    val tree = reify(entity.splice).tree
    val copy = entity.actualType.member(TermName("copy"))

    c.Expr[T](Apply(
      Select(tree, copy),
      AssignOrNamedArg(Ident(TermName("createdOn")), reify(propertyValue.splice).tree) :: Nil
    ))
  }
}
