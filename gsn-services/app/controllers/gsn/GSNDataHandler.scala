package controllers.gsn


import scalaoauth2.provider._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.Date
import models.gsn._
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser
import providers.gsn.GSNUsernamePasswordAuthUser
import scala.collection.JavaConverters._
import play.Logger

class GSNDataHandler extends DataHandler[User] {
  Logger.error("datahandler")

  
  //override def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] =  Future {
    //always require client credentials
    //val c = Client.findById(clientCredential.clientId)
    //c != null && c.secret.equals(clientCredential.clientSecret.getOrElse(""))
  //}

  override def validateClient(maybeClientCredential: Option[ClientCredential],request: AuthorizationRequest): Future[Boolean] =Future{
    Logger.error("1")
    val clientId= maybeClientCredential.map(_.clientId)
    val clientSecret = maybeClientCredential.flatMap(_.clientSecret)
    val c= Client.findById(clientId.getOrElse(""))
    c != null && c.secret.equals(clientSecret.getOrElse(""))
  }

  //override def findUser(username: String, password: String): Future[Option[User]] = Future {
  //  Option(User.findByUsernamePasswordIdentity(new GSNUsernamePasswordAuthUser(username,password)))
  //}

 //newly implemented findUser from DataHandler
  override def findUser( maybeClientCredential: Option[ClientCredential], request: AuthorizationRequest): Future[Option[User]] = Future{
      request match {
          case passwordRequest: PasswordRequest =>
              val username = passwordRequest.username
              val password = passwordRequest.password
              val user = User.findByUsernamePasswordIdentity(new GSNUsernamePasswordAuthUser(username,password))
              Option(user)
          case _ =>
              // Handle other grant types or unknown requests here.
              None
      }
  }
  override def createAccessToken(authInfo: AuthInfo[User]): Future[AccessToken] = Future {
    Logger.error("2")
    val c = Client.findById(authInfo.clientId.getOrElse(""))
    val t = OAuthToken.generate(authInfo.user, c)
    AccessToken(t.token, Option(t.refresh), Some("all"), Some(t.duration/1000),new Date(t.creation))
  }
  
  def getAllTokens(authInfo: AuthInfo[User]): List[OAuthToken] = {
    Logger.error("3")
    val c = Client.findById(authInfo.clientId.getOrElse(""))
    OAuthToken.findByUserClient(authInfo.user, c).asScala.toList
  }

  override def getStoredAccessToken(authInfo: AuthInfo[User]): Future[Option[AccessToken]] = Future {
    Logger.error("3")
    getAllTokens(authInfo).map(t => AccessToken(t.token, Option(t.refresh), Some("all"), Some((t.creation - System.currentTimeMillis + t.duration)/1000),new Date(t.creation))).headOption
  }

  override def refreshAccessToken(authInfo: AuthInfo[User], refreshToken: String): Future[AccessToken] = {
    Logger.error("4")
    // refreshToken already validated
    //clean tokens
    getAllTokens(authInfo).map { x => x.delete() }
    //get a new one
    createAccessToken(authInfo: AuthInfo[User])
  }
  
  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[User]]] = Future {
      Logger.error("4")
      Option(OAuthCode.findByCode(code)).map(c => AuthInfo[User](c.user,Option(c.getClient.getClientId),Some("all"),Option(c.getClient.getRedirect)))
  }

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[User]]] = Future {
    Logger.error("4")
    Option(OAuthToken.findByRefresh(refreshToken)).map(t => AuthInfo[User](t.user,Option(t.getClient.getClientId),Some("all"),Option(t.getClient.getRedirect)))
  }

  def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[User]] = Future {
    Logger.error("4")
    Option(Client.findById(clientCredential.clientId)).flatMap(c => if (c.isLinked) Some(c.user) else None)
  }

  override def deleteAuthCode(code: String): Future[Unit] = Future{
    Logger.error("4")
    Option(OAuthCode.findByCode(code)).map(x => {x.delete()})
  }

  override def findAccessToken(token: String): Future[Option[AccessToken]] = Future {
    Logger.error("4")
    Option(OAuthToken.findByToken(token)).map(t => AccessToken(t.token, Option(t.refresh), Some("all"), Some(t.duration/1000),new Date(t.creation)))
  }

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[User]]] = Future {
    Logger.error("4")
    Option(OAuthToken.findByToken(accessToken.token)).map(t => AuthInfo[User](t.user,Option(t.getClient.getClientId),Some("all"),Option(t.getClient.getRedirect)))
  }

}
