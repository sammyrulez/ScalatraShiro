# ScalatraShiro

[![Build Status](https://travis-ci.org/sammyrulez/ScalatraShiro.svg?branch=master)](https://travis-ci.org/sammyrulez/ScalatraShiro)
[![Coverage Status](https://coveralls.io/repos/sammyrulez/ScalatraShiro/badge.svg)](https://coveralls.io/r/sammyrulez/ScalatraShiro)


ScalatraShiro is an itegration to enable the use of Apache Shiro in Scalatra Application.
It is not a replacement for scalatra auth (Scentry) but exteds it with a authorizazion layer ( roles and permissions)

##Installation

###Maven

```xml
<dependency>
			<groupId>com.github.sammyrulez</groupId>
			<artifactId>scalatra.shiro</artifactId>
			<version>1.0.0</version>
</dependency>
```

### SBT
```scala
libraryDependencies += "com.github.sammyrulez" % "scalatra.shiro" % "1.0.0"
```


##Usage

In order to make use of Shiro in our web application we must first define a Shiro servlet filter. Any requests that we want to secure must go through this Shiro filter.

```xml
<listener>
        <listenerclass>
          org.apache.shiro.web.env.EnvironmentLoaderListener
        </listener-class>
    </listener>

    <filter>
        <filter-name>securityFilter</filter-name>
        <filterclass>
          org.apache.shiro.web.servlet.ShiroFilter
        </filter-class>
    </filter>
    <filter-mapping>
            <filter-name>securityFilter</filter-name>
            <url-pattern>/*</url-pattern>
            <dispatcher>REQUEST</dispatcher>
            <dispatcher>FORWARD</dispatcher>
            <dispatcher>INCLUDE</dispatcher>
            <dispatcher>ERROR</dispatcher>
        </filter-mapping>
```

Configure Shiro through an INI file. For complete details refer to [Shiro Documentation](https://shiro.apache.org/configuration.html)

This is an example of minimal configuration with two users ( 'testuser' and 'admin' ) and two roles ('user' and 'admin')

```
[main]
securityManager.rememberMeManager.cookie.name = rememberMe
# Create a Session Manager
sessionManager = org.apache.shiro.web.session.mgt.DefaultWebSessionManager
securityManager.sessionManager = $sessionManager

# Session Timeout = 1 hour (3600000 miliseconds)
securityManager.sessionManager.globalSessionTimeout = 3600000

sessionDAO = org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO
securityManager.sessionManager.sessionDAO = $sessionDAO

# Login URL:
user.loginUrl = /login

[users]
testUser = password, user
admin = admin, admin

[roles]
user = BasicAccess
admin = *

```

Every controller you want to perform security checks must extends the Authentication Trait

```scala
class MainServlet extends ScalatraServlet with Authentication with ...

```

You might choose to run the authorization checks  in a before() filter in your controller, rather than hitting it in each action, to secure every method. As a best practice you should group routes with same access policies in one controller.


###Login and Logout

If you just have a user/password based authentication a UserAuthServlet is provided by Scalatrashiro. Just register is in your bootstrap class and point to the login/logout routes

If you have complex / custom authentication you can use [Apache Shiro authentication api!](https://shiro.apache.org/authentication.html#Authentication-Step1%3ACollecttheSubject%27sprincipalsandcredentials) directly


```scala
    val currentUser = SecurityUtils.getSubject()

    try {
      val token = // generate a  org.apache.shiro.authc.AuthenticationToken

        currentUser.login(token)
      } catch {
            case uae: UnknownAccountException => {
             ...
            }
            case ice: IncorrectCredentialsException => {
              ...
            }
            case lae: LockedAccountException => {
             ...
            }
            case ae: AuthenticationException => {
              ...
            }
          }

```


###Access control methods

You can check both roles and permissions (https://shiro.apache.org/authorization.html#Authorization-Permissions)

* requiresAuthentication: current user must be authenticated

* requiresRole: current user must have the  role specified as parameter ( or at least one of the role if a List[String] is passed )

* requiresAllRoles: current user must have all the roles in a  List[String] specified  as parameter

* requiresPermission: current user must have the  permission specified as parameter

* requiresAllPermissions: current user must have all the permissions in a  List[String] specified  as parameter




##Credits

Original work on Scalatra  / Apache Shiro integration by Ethan Way (blog post http://ethanway.com/securing-scalatra-with-apache-shiro/ source: https://github.com/waye929/ScalatraShiro)