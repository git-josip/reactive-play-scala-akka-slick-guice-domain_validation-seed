package com.josip.reactiveluxury.module.dao.user.sql

import com.josip.reactiveluxury.module.domain.user._
import org.joda.time.{DateTime, LocalDate}
import com.josip.reactiveluxury.core.slick.postgres.MyPostgresDriver.api._
import com.josip.reactiveluxury.core.slick.custommapper.CustomSlickMapper.Postgres._
import com.josip.reactiveluxury.core.slick.generic.IdentifiableTable

object UserEntityMapper {
  final val USERS_TABLE_NAME = "app_user"

  implicit val userStatusMapper = enumColumnType[UserStatus]
  implicit val userRolwMapper = enumColumnType[UserRole]
  implicit val genderMapper = enumColumnType[Gender]

  class UserTableDescriptor(tag: Tag) extends Table[User](tag, USERS_TABLE_NAME) with IdentifiableTable
  {
    def id              = column[Long]          ("id",          O.PrimaryKey, O.AutoInc )
    def externalId      = column[String]        ("external_id")
    def createdOn       = column[DateTime]      ("created_on")
    def modifiedOn      = column[DateTime]      ("modified_on")

    def status          = column[UserStatus]("status")
    def role            = column[UserRole]("role")
    def firstName       = column[Option[String]]("first_name")
    def lastName        = column[Option[String]]("last_name")
    def email           = column[String]        ("email")
    def password        = column[String]        ("password")
    def dateOfBirth     = column[LocalDate]     ("date_of_birth")
    def gender          = column[Gender]        ("gender")
    def contactAddress  = column[Option[String]]("contact__address")
    def contactCity     = column[Option[String]]("contact__city")
    def contactZip      = column[Option[String]]("contact__zip")
    def contactPhone    = column[Option[String]]("contact__phone")
    def height          = column[Option[Int]]   ("height")
    def weight          = column[Option[Int]]   ("weight")


    def contactDetails = (
      contactAddress,
      contactCity,
      contactZip,
      contactPhone
      ) <>((Contact.apply _).tupled, Contact.unapply)

    def * = (
      id.?,
      externalId.?,
      createdOn.?,
      modifiedOn.?,
      status,
      role,
      firstName,
      lastName,
      email,
      password,
      dateOfBirth,
      gender,
      contactDetails,
      height,
      weight
      ) <> ((User.apply _).tupled, User.unapply)

  }

  object UserTableDescriptor {
    def user = TableQuery[UserTableDescriptor]
  }
}

