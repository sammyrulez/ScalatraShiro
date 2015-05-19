import org.apache.shiro.SecurityUtils
import org.apache.shiro.config.IniSecurityManagerFactory
import org.apache.shiro.web.env.EnvironmentLoaderListener
import org.apache.shiro.web.servlet.ShiroFilter
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterEach
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatra.test.scalatest.ScalatraFlatSpec

import scalatrashiro.MainServlet

@RunWith(classOf[JUnitRunner])
class MainServletSpec extends ScalatraFlatSpec with ShouldMatchers with BeforeAndAfterEach {
  addServlet(new MainServlet, "/*")

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

  "GET /login" should "display a login form" in {
    get("/login") {

      assert(body.contains(
        """
          <form name="loginForm" action="/login" method="post">
        """.trim()))
    }
  }

  "POST /login" should "login a user if username and password supplied are valid" in {
    session {
      post("/login", Map("username" -> "testUser", "password" -> "password")) {
        assert(body.contains("Logged in successfuly."))
      }
    }
  }

  "POST /login" should "fail if an invalid username password combination are supplied" in {
    post("/login", Map("username" -> "testUser", "password" -> "badPassword")) {
      assert(body.contains("Invalid username password combination."))
    }
  }

  "GET /logout" should "logout a user" in {
    session {
      // Login the user.
      post("/login", Map("username" -> "testUser", "password" -> "password")) {}

      get("/logout") {
        assert(body.contains("Logged out successfuly."))
      }
    }
  }

  "GET /auth-required" should "require a user to be logged in" in {
    session {
      // User should be redirected to login page.
      get("/auth-required") {
        assert(response.status == 302)
      }

      // login the user
      post("/login", Map("username" -> "testUser", "password" -> "password")) {}

      get("/auth-required") {
        assert(body.contains("<h2>Must be logged in to view this page.</h2>"))
      }
    }
  }

  "GET /admin-only" should "only be accessable by admin users" in {
    session {
      get("/admin-only") {
        assert(response.status == 302)
      }

      // login the user
      post("/login", Map("username" -> "testUser", "password" -> "password")) {}

      get("/admin-only") {
        assert(body.contains("<h2>401 Forbidden</h2>"))
      }

      // Logout the user.
      get("/logout") {}

      // login the user as admin
      post("/login", Map("username" -> "admin", "password" -> "admin")) {}

      get("/admin-only") {
        assert(body.contains("<h2>Only admins can view this page</h2>"))
      }
    }
  }
}
