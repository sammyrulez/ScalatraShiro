package scalatrashiro

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc._

class MainServlet extends WebStack {
  before() {
    contentType = "text/html"
  }

  get("/") {
    ssp("index.ssp")
  }

  get("/login") {
    ssp("/login")
  }

  post("/login") {
    val username = params.getOrElse("username", "")
    val password = params.getOrElse("password", "")
    val currentUser = SecurityUtils.getSubject()

    try {
      val token = new UsernamePasswordToken(username, password)

      token.setRememberMe(false)
      currentUser.login(token)

      //flash("success") =
      // this += ("success","\"Logged in successfuly.\"")
      this.flash("success") = "Logged in successfuly."

      ssp("index.ssp")
    } catch {
      case uae: UnknownAccountException => {
        this.flash("error") = "Account not found."
        ssp("login.ssp", "username" -> username)
      }
      case ice: IncorrectCredentialsException => {
        this.flash("error") = "Invalid username password combination."
        ssp("login.ssp", "username" -> username)
      }
      case lae: LockedAccountException => {
        this.flash("error") = "Account is locked."
        ssp("login.ssp", "username" -> username)
      }
      case ae: AuthenticationException => {
        throw ae
      }
    }
  }

  get("/logout") {
    val subject = SecurityUtils.getSubject

    if (!subject.isAuthenticated) redirect("/login")

    try {
      subject.logout()
      flash("success") = "Logged out successfuly."
      ssp("index.ssp")
    } catch {
      case e: Exception => throw e
    }
  }

  get("/auth-required") {
    requiresAuthentication()

    ssp("auth-required.ssp")
  }

  get("/perm-required") {
    requiresPermission("BasicAccess")

    ssp("auth-required.ssp")
  }

  get("/perms-required") {
    requiresPermission(List("BasicAccess"))

    ssp("auth-required.ssp")
  }

  get("/all-perms-required") {
    requiresAllPermissions(List("AbstractCon"))
    ssp("auth-required.ssp")
  }

  get("/admin-only") {
    requiresRole("admin")

    ssp("/admin-only.ssp")
  }

  get("/admins-only") {
    requiresRole(List("admin", "super"))

    ssp("/admin-only.ssp")
  }

  get("/super-secure") {
    requiresAllRoles(List("admin", "super"))

    ssp("/admin-only.ssp")
  }
}
