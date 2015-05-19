package scalatrashiro

import org.apache.shiro.SecurityUtils
import org.scalatra.ScalatraBase
import org.scalatra.scalate.ScalateSupport

trait Authentication extends ScalatraBase with ScalateSupport {
  private def currentUser = SecurityUtils.getSubject()

  private def unauthenticated = {
    status = 403
    redirect("/login")
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

    roles.map(r => if (currentUser.hasRole(r)) unauthorized)
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

    permissions.map(p => if (currentUser.isPermitted(p)) unauthorized)
  }
}