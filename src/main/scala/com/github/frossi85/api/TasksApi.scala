package com.github.frossi85.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.github.frossi85.domain.Task
import com.github.frossi85.services.TaskService
import slick.jdbc.JdbcBackend
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import StatusCodes._

trait TasksApi extends AutoMarshaller {
  implicit val db: JdbcBackend#Database = getDatabase

  def getDatabase: JdbcBackend#Database

  val taskService = new TaskService

  def byIdRoutes(id: Int) =
    get {
      complete {
        taskService.byId(id)
      }
    } ~
    (put & entity(as[TaskRequest])) { taskRequest =>
      complete {
        taskService.byId(id).map(t => t match {
          case Some(task) => taskService.update(task.copy(name = taskRequest.name, description = taskRequest.description))
          case None => Future(StatusCodes.NotFound)
        })
      }
    } ~
    delete {
      complete {
        taskService.delete(id).map(x => s"Task with id=$id was deleted")
      }
    }

  def tasksRoutes =
    path("tasks") {
      get {
        complete {
          taskService.byUser(1L)
        }
      } ~
      (post & entity(as[TaskRequest])) { taskRequest =>
        onSuccess(taskService.insert(Task(taskRequest.name, taskRequest.description, 1L))) { task =>
          complete(StatusCodes.Created, task)
        }
      }
    } ~
    path("tasks" / IntNumber) { id => byIdRoutes(id) }
}

