package com.josip.reactiveluxury.core.jwt

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.{Reads, Writes, JsValue, Json}
import com.nimbusds.jose.{Payload, JWSAlgorithm, JWSHeader, JWSObject}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object JwtUtil {
  def signJwtPayload(payload : String)(implicit secret : JwtSecret) : String = {
    Asserts.argumentIsNotNullNorEmpty(payload)
    Asserts.argumentIsNotNull(secret)

    val jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(payload))
    jwsObject.sign(secret.signer)
    jwsObject.serialize
  }

  def signJwtPayload(payload : JsValue)(implicit secret : JwtSecret) : String = {
    Asserts.argumentIsNotNull(payload)
    Asserts.argumentIsNotNull(secret)

    this.signJwtPayload(payload.toString())
  }

  def signJwtPayload[T](payload : T)(implicit secret : JwtSecret, jsonWrites : Writes[T]) : String = {
    Asserts.argumentIsNotNull(payload)
    Asserts.argumentIsNotNull(secret)
    Asserts.argumentIsNotNull(jsonWrites)

    this.signJwtPayload(Json.toJson(payload))
  }

  def tryGetPayloadStringIfValidToken(token : String)(implicit secret : JwtSecret) : Future[Option[String]] = {
    Asserts.argumentIsNotNull(token)
    Asserts.argumentIsNotNull(secret)

    try {
      val jwsObject = JWSObject.parse(token)

      jwsObject.verify(secret.verifier) match
      {
        case true  => Future.successful(Some(jwsObject.getPayload.toString))
        case false => Future.successful(None)
      }
    }
    catch {
      case  _ : Throwable => Future.successful(None)
    }
  }

  def getPayloadIfValidToken[T](token : String)(implicit secret : JwtSecret, jsonWrites : Reads[T]) : Future[Option[T]] = {
    Asserts.argumentIsNotNull(token)
    Asserts.argumentIsNotNull(secret)
    Asserts.argumentIsNotNull(jsonWrites)

    this.tryGetPayloadStringIfValidToken(token).flatMap {
      case Some(sv) => Future.successful(Some(Json.parse(sv).as[T]))
      case None => Future.successful(None)
    }
  }
}
