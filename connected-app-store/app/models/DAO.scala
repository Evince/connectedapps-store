package models

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }


@Singleton
class DAO @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec:ExecutionContext){
  private val dbConfig  = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  private class CategoryTable(tag: Tag) extends Table[Category](tag, "category") {

    def id = column[UUID]("id",O.PrimaryKey)

    def category_name = column[String]("category_name")

    def * = ( id, category_name) <> ((Category.apply _).tupled, Category.unapply)
  }

  private val category = TableQuery[CategoryTable]

  // Add Category
  def createCategory(cat: Category) = db.run{
    category += cat
  }

  // List all category
  def getAllCategory(): Future[Seq[Category]] = db.run(category.sortBy(_.category_name.asc).result)


  //  Change category name
  def updateIncidence(cat: Category) = db.run{
    category.filter(_.id === cat.id ).update(cat)
  }

  // Removing Incident record
  def removeCategory(cat: Category) = db.run(
    category.filter(_.id === cat.id).delete
  )

  private class ApplicationTable(tag: Tag) extends Table[connectedApplication](tag, "applicationname") {

    def name = column[String]("app_name",O.PrimaryKey)

    def appCategory = column[UUID]("app_category")

    def description = column[String]("description")

    def rating = column[Int]("rating")    

    def image = column[String]("image")

    def longDescription = column[String]("longdescription")

    def cost = column[Double]("cost")

    def * = (name, appCategory, description, rating, image, longDescription, cost) <> ((connectedApplication.apply _).tupled, connectedApplication.unapply)

    def category_fk_Application = foreignKey("categoryKey",appCategory, category )( _.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  }

  private val application = TableQuery[ApplicationTable]

  // Add new application
  def addApplication( app : connectedApplication) = db.run{
    application += app
  }

  // Get all Application
  def getAllApplication(category : UUID) : Future[Seq[connectedApplication]] = db.run{
    application.filter(_.appCategory === category ).result
  }

  // Get application by App name
  def getApplicationByName(name:String): Future[Option[connectedApplication]]= db.run{
    application.filter(_.name === name).result.headOption
  }

  //Get list of all Applications
  def ApplicationList: Future[Seq[String]]= db.run{
    application.map(_.name).result
  }

  // Search for matches
  def searchApplication(appName: String): Future[Seq[connectedApplication]]= db.run{
    application.filter(_.name.toLowerCase  like s"%$appName%".toLowerCase ).result
  }


  // Remove an application
  def removeApplication(app : String) = db.run{
    application.filter(_.name === app).delete
  }

  private class RequirementTable(tag: Tag) extends Table[Requirement](tag, "requirements") {

    def id = column[Long]("id",O.PrimaryKey)

    def name = column[String]("app_name")

    def requiretext = column[String]("app_requirement")

    def * = (id, name, requiretext) <> ((Requirement.apply _).tupled, Requirement.unapply)

    def Requirment_fk_Application = foreignKey("requirementKey",name, application )( _.name, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  }

  private val require = TableQuery[RequirementTable]

  def getApplicationRequirmentByName(name: String): Future[Seq[Requirement]]= db.run{
    require.filter(_.name === name).result
  }

  private class BearerTable(tag: Tag) extends Table[Bearer](tag, "bearer") {

    def id = column[Long]("id",O.PrimaryKey)

    def name = column[String]("app_name")

    def bearertext = column[String]("app_bearer")

    def * = (id, name, bearertext) <> ((Bearer.apply _).tupled, Bearer.unapply)

    def Bearer_fk_Application = foreignKey("bearerKey",name, application )( _.name, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  }

  private val bear = TableQuery[BearerTable]

  def getApplicationBearerByName(name: String): Future[Seq[Bearer]]= db.run{
    bear.filter(_.name === name).result
  }

  private class TagTable(tag: Tag) extends Table[AppTag](tag, "tag") {

    def id = column[Long]("id",O.PrimaryKey)

    def name = column[String]("app_name")

    def apptag = column[String]("app_tag")

    def * = (id, name, apptag) <> ((AppTag.apply _).tupled, AppTag.unapply)

    def Tag_fk_Application = foreignKey("tagKey",name, application )( _.name, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  }

  private val mytag = TableQuery[TagTable]

  def getApplicationTagByName(name: String): Future[Seq[AppTag]]= db.run {
    mytag.filter(_.name === name).result
  }

  private class ActivationTable(tag: Tag) extends Table[Activation](tag, "activation") {

    def id = column[Long]("id", O.PrimaryKey)

    def name = column[String]("app_name")

    def active = column[String]("app_activation")

    def * = (id, name, active) <> ((Activation.apply _).tupled, Activation.unapply)

    def Activation_fk_Application = foreignKey("ActivationKey", name, application)(_.name, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  }

  private val activation =  TableQuery[ActivationTable]

  def getApplicationactivationByName(name: String): Future[Seq[Activation]] = db.run {
    activation.filter(_.name === name).result
  }
}
