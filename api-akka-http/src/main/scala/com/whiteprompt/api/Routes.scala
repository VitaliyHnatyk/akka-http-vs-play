package com.whiteprompt.api

import akka.http.scaladsl.server.Directives._

trait Routes extends TaskRoutes {

  val routes =
    pathPrefix("v1") {
      tasksRoutes
    } ~
    path("health-check"){
      get {
        complete("Akka HTTP API: up and running!")
      }
    }
}