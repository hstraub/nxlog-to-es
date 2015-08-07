package at.linuxhacker.nxlogToEs.es

import akka.actor.{ Actor, ActorSystem, Props, ActorLogging }
import play.api.libs.json._
import scalaj.http._
import scala.concurrent.duration._

class BulkSender extends Actor with ActorLogging {
  
  log.info( "BulkSender started")
  
  var messages = List[String]( )
  val bulkSize = 1000
  val bulkTimewindow = 5
  val index = Json.stringify( Json.obj( "index" -> Json.obj( "_index" -> "nxlog", "_type" -> "simple" ) ) )
  
  import context.dispatcher
  case object BulkTick
  val bulkTick = context.system.scheduler.schedule( 5 seconds, 5 seconds, self, BulkTick )
  
  
  def receive = {
    
    case json: JsObject =>
      val jsonString = Json.stringify( json )
      messages = messages :+ jsonString
      if ( messages.size >= bulkSize )
        sendMessages
        
    case BulkTick =>
      if ( messages.size > 0 )
        sendMessages
  }
  
  def sendMessages( ) = {
   
    val buffer = new scala.collection.mutable.StringBuilder()
    messages.foreach { x =>
      buffer.append( index + "\n" )
      buffer.append( x + "\n" )
    }

    val bulk = buffer.mkString
    val resp = Http( "http://localhost:9200/_bulk" )
      .headers( "content-type" -> "application/json" )
      .method( "POST" )
      .postData( bulk )
      .asString
      
    if ( !resp.isSuccess )
      log.error( "ES error: " + resp.statusLine )

    log.info( "BulkSender sent " + messages.size + " messages.")
    messages = List[String]( )
  }
  
}