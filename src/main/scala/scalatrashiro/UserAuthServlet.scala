package scalatrashiro

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc._
import org.scalatra.{FlashMapSupport, ScalatraServlet}

/**
 * Created by sam on 22/05/15.
 */
class UserAuthServlet(val afterAction: String, val loginUrl: String) extends ScalatraServlet with FlashMapSupport {


  post("/login") {
    val username = params.getOrElse("username", "")
    val password = params.getOrElse("password", "")
    val currentUser = SecurityUtils.getSubject()

    try {
      val token = new UsernamePasswordToken(username, password)

      token.setRememberMe(false)
      currentUser.login(token)

      this.flash("success") = "Logged in successfuly."

      redirect(afterAction)


    } catch {
      case uae: UnknownAccountException => {
        this.flash("error") = "Account not found."
        redirect(loginUrl)
      }
      case ice: IncorrectCredentialsException => {
        this.flash("error") = "Invalid username password combination."
        redirect(loginUrl)
      }
      case lae: LockedAccountException => {
        this.flash("error") = "Account is locked."
        redirect(loginUrl)
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
      redirect(afterAction)
    } catch {
      case e: Exception => throw e
    }
  }

}
