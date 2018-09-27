package controllers

import java.util.UUID
import javax.inject._
import play.api._
import play.api.mvc._
import models._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(cc: ControllerComponents, repo: DAO)
                              (implicit ec: ExecutionContext)
                              extends AbstractController(cc) {

  def index() = Action.async { implicit request =>    
    repo.getAllCategory.map ( result =>        
     Ok(views.html.index(result)) 
    )
  }

  def error() = Action.async { implicit request =>
      ???
  }

//   def appDetail( category: String, name: String) = Action.async { implicit request =>
//        val result =for{
//      detail <-   repo.getApplicationByName(name)
//
//  } yield (detail)
//    result.map {  detail =>
//      detail match {
//            case Some(x) => Ok(views.html.app_detail(x,category))
//            case None => Ok
//      }
//    }
//  }

  def appDetail( category: String, name: String) = Action.async { implicit request =>

    repo.getApplicationByName(name) flatMap { app =>
      repo.getApplicationRequirmentByName(name) flatMap { req =>
        repo.getApplicationBearerByName(name) flatMap { bearer =>
          repo.getApplicationTagByName(name) flatMap { tag =>
            repo.getApplicationactivationByName(name) map { active =>
              app match{
                case Some(e) => Ok(views.html.app_detail(e,req,bearer,tag,active,name,category))
                case None => BadRequest
              }
            }
          }
        }
      }
    }
   }



  def appList( category: UUID, name:String) = Action.async { implicit request =>      
      repo.getAllApplication(category)
      .map( result =>
        Ok(views.html.app_list(result,name))
      )      
  }

  def searchList = Action.async(parse.formUrlEncoded) { implicit request =>   
      val name = request.body.get("search").get.mkString.trim    
      repo.searchApplication(name).map( result =>
        Ok(views.html.search_result(result,name))
      )                  
  }
}
