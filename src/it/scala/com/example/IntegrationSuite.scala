package com.example

import cats.data.Chain
import weaver.{Result, SimpleIOSuite, TestOutcome}

import scala.concurrent.duration._


object IntegrationSuite extends SimpleIOSuite {

  private def registerSuite(suite: SimpleIOSuite): Unit = {
    suite.plan.foreach(testName =>
      IntegrationSuite.registerTest(testName)(
        _ => suite.spec(List(testName.name)).compile.last.map(_.getOrElse(
          TestOutcome("setup fail", 0.seconds, Result.from(new RuntimeException("")), Chain.empty)
        ))
      )
    )
  }

  registerSuite(HelloWorldSuite)
  registerSuite(TodoSuite)
}
