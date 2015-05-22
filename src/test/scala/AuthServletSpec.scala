import org.apache.shiro.SecurityUtils
import org.apache.shiro.config.IniSecurityManagerFactory
import org.apache.shiro.web.env.EnvironmentLoaderListener
import org.apache.shiro.web.servlet.ShiroFilter
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterEach
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatra.test.scalatest.ScalatraFlatSpec

import scalatrashiro.{MainServlet, UserAuthServlet}

@RunWith(classOf[JUnitRunner])
class AuthServletSpec extends ScalatraFlatSpec with ShouldMatchers with BeforeAndAfterEach {
  addServlet(new MainServlet, "/*")
  addServlet(new UserAuthServlet("/", "/login"), "/account/*")

  // Add listener and filter for Shiro Session
  servletContextHandler.addEventListener(new EnvironmentLoaderListener)
  addFilter(classOf[ShiroFilter], "/*")

  protected override def beforeEach() {
    val factory = new IniSecurityManagerFactory("src/test/resources/shiro.ini")
    val securityManager = factory.getInstance
    SecurityUtils.setSecurityManager(securityManager)
  }

  protected override def afterEach() {

  }


  "POST /account/login" should "login a user if username and password supplied are valid" in {
    session {
      post("/account/login", Map("username" -> "testUser", "password" -> "password")) {
        assert(status == 302)
      }
    }
  }

  "POST /account/login" should "fail if an invalid username password combination are supplied" in {
    post("/account/login", Map("username" -> "testUser", "password" -> "badPassword")) {
      assert(status == 302)
      // assert(flash.contains("Invalid username password combination."))
    }
  }

  "GET /account/logout" should "logout a user" in {
    session {
      // Login the user.
      post("/account/login", Map("username" -> "testUser", "password" -> "password")) {}

      get("/account/logout") {
        // assert(body.contains("Logged out successfuly."))
        assert(status == 302)
      }
    }
  }


}
