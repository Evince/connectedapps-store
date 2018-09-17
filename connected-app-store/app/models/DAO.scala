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

  private class ApplicationTable(tag: Tag) extends Table[Application](tag, "applicationname") {

        def name = column[String]("app_name")  

        def appCategory = column[UUID]("app_category")  

        def id = column[Long]("id",O.PrimaryKey)

        def description = column[String]("description")

        def rating = column[Int]("rating")

        def size = column[Double]("app_size")

        def image = column[String]("image")

        def longDescription = column[String]("longdescription")
        
        def * = (name, appCategory, id, description, rating,size, image, longDescription) <> ((Application.apply _).tupled, Application.unapply)

        def category_fk_Application = foreignKey("categoryKey",appCategory, category )( _.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    }

    private val application = TableQuery[ApplicationTable]

// Add new application
  def addApplication( app : Application) = db.run{         
       application += app
  } 

// Get all Application
  def getAllApplication(category : UUID) : Future[Seq[Application]] = db.run{    
      application.filter(_.appCategory === category ).result
  }

// Get application by App name
  def getApplicationByName(name: String): Future[Option[Application]]= db.run{
    application.filter(_.name === name).result.headOption
  }

  // Search for matches
  def searchApplication(appName: String): Future[Seq[Application]]= db.run{
    application.filter(_.name like s"%$appName%" ).result
  }


// Remove an application
  def removeApplication(app : String) = db.run{
      application.filter(_.name === app).delete
  }

}
