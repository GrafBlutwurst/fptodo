package com.example

import endpoints4s.openapi
import endpoints4s.openapi.model.OpenApi
import io.swagger.parser.OpenAPIParser
import io.swagger.util.Yaml

import java.io.File

object OpenApiGenerator {
  def main(args: Array[String]): Unit = {

    val endpoints = new DomainEndpoints
      with openapi.Endpoints
      with openapi.JsonEntitiesFromSchemas {

      val docs = openApi(openapi.model.Info("FP Todo", "0.1"))(
        helloWorld,
        getTodo,
        getTodos,
        createTodo
      )
    }

    val specs = List(
      "openapi" -> endpoints.docs
    )

    val specStrings = specs.map { case (name, spec) =>
      name -> OpenApi.stringEncoder.encode(spec)
    }

    val parser = new OpenAPIParser()

    val openApis = specStrings.map { case (name, specString) =>
      name -> parser.readContents(specString, null, null).getOpenAPI()
    }

    openApis.foreach { case (name, openApi) =>
      val file = new File(s"$name.yml")
      Yaml.mapper().writeValue(file, openApi)
    }

    println("Generation finished")
  }


}
