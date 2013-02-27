package com.prealpha.sge.networking.networking.messagetests

import com.prealpha.sge.messages.HandshakeMessage
import com.prealpha.sge.networking.networking.{MasterServer, Server, Client}
import java.net.Socket

object BlockingWaitTest extends App {

  case object ImHereMessage extends HandshakeMessage
  case object ReceivedMessage extends HandshakeMessage

  case class MyClient(ip: String, port: Int) extends Client("localhost", 1111) {
    write(ImHereMessage)
    messagePublisher.blockUntilObject(ReceivedMessage)
    println("client done")

  }

  case class MyServer(socket: Socket) extends Server(socket) {
    messagePublisher.blockUntilObject(ImHereMessage)
    write(ReceivedMessage)
    println("server done")

  }

  val masterServer = new MasterServer(1111, MyServer.apply)
  val client = new MyClient("localhost", 1111)
}
