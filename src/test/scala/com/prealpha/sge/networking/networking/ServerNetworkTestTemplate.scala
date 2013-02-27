package com.prealpha.sge.networking.networking

import java.net.Socket
import com.prealpha.sge.messages.HandshakeMessage


object ServerNetworkTestTemplate extends App {

  case object ImHereMessage extends HandshakeMessage

  case object ReceivedMessage extends HandshakeMessage

  case class MyClient(ip: String, port: Int) extends Client("localhost", 1111) {

  }

  case class MyServer(socket: Socket) extends Server(socket) {

  }

  val masterServer = new MasterServer(1111, MyServer.apply)
  val client = new MyClient("localhost", 1111)
}
