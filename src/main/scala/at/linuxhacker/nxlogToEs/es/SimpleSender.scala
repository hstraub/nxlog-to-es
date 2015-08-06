package at.linuxhacker.nxlogToEs.es

import akka.actor.{ Actor, ActorSystem, Props, ActorLogging }
import play.api.libs.json._
import scalaj.http._

class SimpleSender extends Actor with ActorLogging {
  
  def receive = {
    case json: JsObject =>
      val jsonString = Json.stringify( json ) 
      val esResp =  Http( "http://localhost:9200/nxlog/simple" )
            .headers( "content-type" -> "application/json" )
            .method( "POST" )
            .postData( jsonString )
            .asString
          println( esResp.statusLine )
      
  }
  
}