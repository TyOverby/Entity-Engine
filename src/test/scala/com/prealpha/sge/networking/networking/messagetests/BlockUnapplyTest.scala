package com.prealpha.sge.networking.networking.messagetests

import java.net.Socket
import com.prealpha.sge.messages.HandshakeMessage
import com.prealpha.sge.networking.networking._


object BlockUnapplyTest extends App {

  case object ImHereMessage extends HandshakeMessage

  case object ReceivedMessage extends HandshakeMessage

  case class SomeDataHere(str: String, int: Int) extends HandshakeMessage

  case class MyClient(ip: String, port: Int) extends Client("localhost", 1111) {
    write(ImHereMessage)
    println(messagePublisher.blockUnapply(SomeDataHere.unapply))

    println(SomeDataHere.getClass)

  }

  case class MyServer(socket: Socket) extends Server(socket) {
    messagePublisher.blockUntilObject(ImHereMessage)
    write(ImHereMessage)
    write(SomeDataHere("hi", 5))
  }

  val masterServer = new MasterServer(1111, MyServer.apply)
  val client = new MyClient("localhost", 1111)
}
