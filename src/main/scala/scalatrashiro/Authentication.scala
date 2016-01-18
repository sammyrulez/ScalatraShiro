package scalatrashiro

import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import org.scalatra.ScalatraBase
import org.scalatra.auth.{ScentryConfig, ScentrySupport}
import org.scalatra.scalate.ScalateSupport

trait Authentication extends ScalatraBase with ScalateSupport with ScentrySupport[Subject] {

  private def currentUser = SecurityUtils.getSubject()

  protected val loginUrl: String

  protected def fromSession = {
    case id: String => currentUser
  }

  protected def toSession = {
    case usr: Subject => usr.getPrincipal.toString
  }

  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]



  private def unauthenticated = {
    status = 403
    redirect(loginUrl)
  }

  private def unauthorized = {
    halt(401, ssp("error/401.ssp"))
  }

  def requiresAuthentication() {
    if (!currentUser.isAuthenticated())
      unauthenticated
  }

  def requiresRole(role: String) {
    requiresAuthentication()

    if (!currentUser.hasRole(role))
      unauthorized
  }

  def requiresRole(roles: List[String]) {
    requiresAuthentication()

    roles.map(r => if (currentUser.hasRole(r)) return)
    unauthorized
  }

  def requiresAllRoles(roles: List[String]) {
    requiresAuthentication()

    roles.map(r => if (!currentUser.hasRole(r)) unauthorized)
  }

  def requiresPermission(permission: String) {
    requiresAuthentication()

    if (!currentUser.isPermitted(permission))
      unauthorized
  }

  def requiresPermission(permissions: List[String]) {
    requiresAuthentication()

    permissions.map(p => if (currentUser.isPermitted(p)) return)
    unauthorized
  }

  def requiresAllPermissions(permissions: List[String]) {
    requiresAuthentication()

    permissions.map(p => if (!currentUser.isPermitted(p)) unauthorized)
  }
}