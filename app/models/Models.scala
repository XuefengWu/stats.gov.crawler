package models


import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

trait DBCode

case object hgydks extends DBCode

case object hgjdks extends DBCode

case object hgndks extends DBCode

case object fsydks extends DBCode

case object fsjdks extends DBCode

case object fsndks extends DBCode

trait Dimension

case object reg extends Dimension

case object sj extends Dimension

case object zb extends Dimension


case class Data(a: String, decode: String, region: String, index: String, date: String, value: Double)

case class Index(dbcode: String, id: String, pId: String, name: String, isParent: Boolean, ifData: Option[Int], unit: Option[String] = None)

case class Moment(dimension: String, dbcode: String, value: String)

case class Region(id: String, name: String)


object Data {

  val simple = {
    get[String]("data.a") ~
      get[String]("data.decode") ~
      get[String]("data.region") ~
      get[String]("data.`index`") ~
      get[String]("data.`date`") ~
      get[Double]("data.`value`") map {
      case a ~ decode ~ region ~ index ~ date ~ value => Data(a, decode, region, index, date, value)
    }
  }

  def insert(values: Seq[Data]) = {
    DB.withConnection {
      implicit connection =>
        val insertQuery = SQL( """insert into data(a,decode,region,`index`,`date`,`value`) values (
                             {a},{decode},{region},{index},{date},{value})"""
        )

        values.foreach {
          elem =>
            if (isNotExist(elem)) {
              insertQuery.on(
                'a -> elem.a,
                'decode -> elem.decode,
                'region -> elem.region,
                'index -> elem.index,
                'date -> elem.date,
                'value -> elem.value
              ).executeUpdate()
            }
        }
    }
  }

  def isNotExist(elem: Data) = {
    DB.withConnection {
      implicit connection =>
        val totalRows = SQL(
          """
          select count(id) from data
          where a = {a} and decode = {decode} and region = {region} and `index` = {index} and date = {date}
          """
        ).on(
            'a -> elem.a,
            'decode -> elem.decode,
            'region -> elem.region,
            'index -> elem.index,
            'date -> elem.date
          ).as(scalar[Long].single)
        totalRows == 0
    }
  }


}

object Index {

  val region = {
    get[String]("region.id") ~
      get[String]("region.name") map {
      case id ~ name => Region(id, name)
    }
  }

  implicit def byteToBoolean(isParent: Byte): Boolean = isParent == 1

  val simple = {
    get[String]("dbcode") ~
      get[String]("id") ~
      get[String]("pid") ~
      get[String]("name") ~
      get[Boolean]("isParent") ~
      get[Option[Int]]("ifData") ~
      get[Option[String]]("unit") map {
      case dbcode ~ id ~ pid ~ name ~ isParent ~ ifData ~ unit => Index(dbcode, id, pid, name, isParent, ifData, unit)
    }
  }

  def insert(values: Seq[Index]) = {
    DB.withConnection {
      implicit connection =>
        val insertQuery = SQL( """insert into `index`(dbcode,id,pid,name,isParent,ifData) values (
                                {dbcode},{id},{pid},{name},{isParent},{ifData})"""
        )

        values.foreach {
          elem =>
            if (isNotExist(elem)) {
              insertQuery.on(
                'dbcode -> elem.dbcode,
                'id -> elem.id,
                'pid -> elem.pId,
                'name -> elem.name,
                'isParent -> elem.isParent,
                'ifData -> elem.ifData
              ).executeUpdate()
            }
        }
    }
  }

  def isNotExist(elem: Index) = {
    DB.withConnection {
      implicit connection =>
        val totalRows = SQL(
          """
          select count(id) from `index`
          where id = {id} and dbcode = {dbcode}
          """
        ).on(
            'id -> elem.id,
            'dbcode -> elem.dbcode
          ).as(scalar[Long].single)
        totalRows == 0
    }
  }

  def fetchDataIndexs(dbcode: String): Seq[Index] = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          select * from `index`
          where dbcode = {dbcode} and ifdata = 1
          order by id desc
          """
        ).on(
            'dbcode -> dbcode
          ).as(simple *)
    }
  }

  def fetchAllDataIndexs(): Seq[Index] = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          select * from `index`
          order by dbcode,id
          """
        ).as(simple *)
    }
  }


  def fetchRegion(): Seq[Region] = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          select id,name from `region`
          """
        ).as(region *)
    }
  }

  def getIndex(dbcode: String, id: String): Option[Index] = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          select * from `index`
          where dbcode = {dbcode} and id = {id}
          """
        ).on(
            'dbcode -> dbcode,
            'id -> id
          ).as(simple.singleOpt)
    }
  }

  def updateUnit(dbcode: String, id: String, unit: String) = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          update `index` set `unit` = {unit}
          where dbcode = {dbcode} and id = {id}
          """
        ).on(
            'dbcode -> dbcode,
            'id -> id,
            'unit -> unit
          ).executeUpdate()
    }

  }

}

object Moment {

  def insert(values: Seq[Moment]) = {
    DB.withConnection {
      implicit connection =>
        val insertQuery = SQL( """insert into moment(dimension,dbcode,`value`) values (
                                {dimension},{dbcode},{value})"""
        )

        values.foreach {
          elem =>
            if (isNotExist(elem)) {
              insertQuery.on(
                'dimension -> elem.dimension,
                'dbcode -> elem.dbcode,
                'value -> elem.value
              ).executeUpdate()
            }
        }
    }
  }

  def isNotExist(elem: Moment) = {
    DB.withConnection {
      implicit connection =>
        val totalRows = SQL(
          """
          select count(id) from `moment`
          where dimension = {dimension} and dbcode = {dbcode} and `value` = {value}
          """
        ).on(
            'dimension -> elem.dimension,
            'dbcode -> elem.dbcode,
            'value -> elem.value
          ).as(scalar[Long].single)
        totalRows == 0
    }
  }

}