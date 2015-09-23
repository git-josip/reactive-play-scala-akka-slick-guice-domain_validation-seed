package com.josip.reactiveluxury.module.generalsettings.dao.sql.mapper

import com.josip.reactiveluxury.module.generalsettings.domain.{CompanyData, BusinessData, GeneralSettingsDetailsEntity}
import com.josip.reactiveluxury.module.generalsettings.domain.CompanyData
import slick.driver.PostgresDriver.api._

object GeneralSettingsMapper
{
  private final val TABLE_NAME = "general_settings"

  private final val ID_COLUMN                         = "id"
  private final val COMPANY_DATA__COUNTRY_COLUMN      = "company_data__country"
  private final val COMPANY_DATA__STREET_COLUMN       = "company_data__street"
  private final val COMPANY_DATA__CITY_COLUMN         = "company_data__city"
  private final val COMPANY_DATA__ZIP_COLUMN          = "company_data__zip"
  private final val COMPANY_DATA__PHONE_COLUMN        = "company_data__phone"
  private final val COMPANY_DATA__MOBILE_COLUMN       = "company_data__mobile"
  private final val COMPANY_DATA__OIB_COLUMN          = "company_data__oib"
  private final val BUSINESS_DATA__PROFIT_PERCENTAGE  = "business_data__profit_percentage"

  class GeneralSettingsDetailsEntityTableDescriptor(tag: Tag) extends Table[GeneralSettingsDetailsEntity](tag, TABLE_NAME)
  {
    def id                            = column[Long]  (ID_COLUMN,                         O.PrimaryKey, O.AutoInc )
    def companyDataCountry            = column[String](COMPANY_DATA__COUNTRY_COLUMN                     )
    def companyDataStreet             = column[String](COMPANY_DATA__STREET_COLUMN                     )
    def companyDataCity               = column[String](COMPANY_DATA__CITY_COLUMN                       )
    def companyDataZip                = column[String](COMPANY_DATA__ZIP_COLUMN                         )
    def companyDataPhone              = column[String](COMPANY_DATA__PHONE_COLUMN                       )
    def companyDataMobile             = column[String](COMPANY_DATA__MOBILE_COLUMN                      )
    def companyDataOib                = column[String](COMPANY_DATA__OIB_COLUMN                         )
    def businessDataProfitPercentage  = column[Int]   (BUSINESS_DATA__PROFIT_PERCENTAGE                 )

    def * = {
      val shapedValue = (
        id,
        companyDataCountry,
        companyDataStreet,
        companyDataCity,
        companyDataZip,
        companyDataPhone,
        companyDataMobile,
        companyDataOib,
        businessDataProfitPercentage
      ).shaped

      shapedValue.<> (
        {
          tuple =>
            GeneralSettingsDetailsEntity(
              id = tuple._1,
              companyData = CompanyData(
                country = tuple._2,
                street  = tuple._3,
                city    = tuple._4,
                zip     = tuple._5,
                phone   = tuple._6,
                mobile  = tuple._7,
                oib     = tuple._8
              ),
              businessData = BusinessData(profitPercentage = tuple._9)
            )
        }, {
        (v: GeneralSettingsDetailsEntity) =>
          Some{
            ( v.id,
              v.companyData.country,
              v.companyData.street,
              v.companyData.city,
              v.companyData.zip,
              v.companyData.phone,
              v.companyData.mobile,
              v.companyData.oib,
              v.businessData.profitPercentage
            )
          }
      })
    }
  }

  object GeneralSettingsDetailsEntityTableDescriptor
  {
    def query = TableQuery[GeneralSettingsDetailsEntityTableDescriptor]
  }
}
