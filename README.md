# ScalatraShiro

[![Build Status](https://travis-ci.org/sammyrulez/ScalatraShiro.svg?branch=master)](https://travis-ci.org/sammyrulez/ScalatraShiro)
[![Coverage Status](https://coveralls.io/repos/sammyrulez/ScalatraShiro/badge.svg)](https://coveralls.io/r/sammyrulez/ScalatraShiro)

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

Configure Shiro through an INI file. For complete details refer to Shiro Documentation https://shiro.apache.org/configuration.html

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

Every servlet you want to perform security checks must extends the Authentication Trait

```scala
class MainServlet extends ScalatraServlet with Authentication with ...

```






##Credits

Original work on Scalatra  / Apache Shiro integration by Ethan Way (blog post http://ethanway.com/securing-scalatra-with-apache-shiro/ source: https://github.com/waye929/ScalatraShiro)