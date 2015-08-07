package at.linuxhacker.nxlogToEs.message

import akka.actor.{ Actor, ActorSystem, Props, ActorLogging }
import akka.util.ByteString
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import org.joda.time.DateTime

class Parser extends Actor with ActorLogging {
  
  val es = context.actorSelection( "akka://NxlogToEs/user/BulkSender")
  
  val transformEventReceivedTime = ( __  ).json.pickBranch(
      ( __ \ 'EventReceivedTime ).json.update(
          of[JsNumber].map { case JsNumber( ts ) =>
            val timestring = new DateTime( ts.toLong * 1000l )
            JsString( timestring.toString ) }
      )
  )
  val transformHostname = ( __ ).json.copyFrom( ( __ ).read[JsObject].map { o =>
    val keys = o.keys
    val fqdn = ( o \ "Hostname" ).as[String].toLowerCase
    val hostname = fqdn.split( "\\." )(0)
    o ++ Json.obj( "fqdn" -> fqdn, "hostname" -> hostname)
  }) andThen ( __ \ 'Hostname ).json.prune
  val transformEventTime = ( __ \ 'EventTime ).json.prune
  val transformEventTimeWritten = ( __ \ 'EventTimeWritten ).json.prune
 
  val transformer = 
      transformEventReceivedTime andThen 
      transformEventTime andThen 
      transformEventTimeWritten andThen
      transformHostname
  
  def receive = {
    
    case data: ByteString =>
      val dataString = data.decodeString( "utf-8" )
      val json = Json.parse( dataString )
      val result = json.transform( transformer )
      result match {
        case JsSuccess( obj, path ) =>
          es ! obj
          
        case JsError( errors ) =>
          log.error( "Json Transformer errors: " + errors )
      }
  }
}