package com.github.frossi85.api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.frossi85.database.TestDB
import com.github.frossi85.{ServicesModule, ConfigModule}
import com.google.inject.{Guice, Injector}
import kamon.Kamon
import org.scalatest._

abstract class ApiSpec extends WordSpec
  with Matchers
  with ScalatestRouteTest
  with BeforeAndAfterEach
  with TestDB
{
  val injector: Injector = Guice.createInjector(
    new ConfigModule(),
    new ServicesModule()
  )

  override protected def beforeEach() {
    super.beforeEach()
    initializeRepository()
    Kamon.start()
  }

  override protected def afterEach() {
    cleanUpRepository()
    Kamon.shutdown()
    super.afterEach()
  }
}
