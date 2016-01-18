package scalatrashiro

import javax.servlet.http.HttpServletRequest

import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.layout.DefaultLayoutStrategy
import org.scalatra._
import org.scalatra.scalate.ScalateSupport

import scala.collection.mutable

trait WebStack extends ScalatraServlet with ScalateSupport with MethodOverride with FlashMapSupport with Authentication {
  /* wire up the precompiled templates */
  override protected def defaultTemplatePath: List[String] = List("src/test/webapp/WEB-INF/templates/views")

  override protected def createTemplateEngine(config: ConfigT) = {
    val engine = super.createTemplateEngine(config)
    engine.layoutStrategy = new DefaultLayoutStrategy(engine,
      TemplateEngine.templateTypes.map("src/test/webapp//WEB-INF/templates/layouts/default." + _): _*)
    engine.packagePrefix = "templates"
    engine
  }

  /* end wiring up the precompiled templates */

  override protected def templateAttributes(implicit request: HttpServletRequest): mutable.Map[String, Any] = {
    super.templateAttributes ++ mutable.Map.empty // Add extra attributes here, they need bindings in the build file
  }

  notFound {
    contentType = null
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse display404
  }

  protected def display404 = {
    contentType = "text/html"
    response.setStatus(404)
    ssp("/error/404.ssp")
  }

  override protected val loginUrl: String = "/login"
}