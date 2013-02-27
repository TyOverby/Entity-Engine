package com.prealpha.sge.networking.networking.messagetests

import java.net.Socket
import com.prealpha.sge.messages.HandshakeMessage
import com.prealpha.sge.networking.networking._
import com.prealpha.sge.networking.ConStarter._


object BlockUnapplyTest extends App {

  case object ImHereMessage extends HandshakeMessage

  case class SomeDataHere(str: String, int: Int) extends HandshakeMessage

  case class MyClient(ip: String, port: Int) extends Client("localhost", 1111) {
    val clienthandshake = this >> ImHereMessage << SomeDataHere.unapply << SomeDataHere.unapply
    val rec = clienthandshake()
    println("CLIENT: "+rec)

    val List((a: String, b: Int), (c:String, d: Int))  = rec
    println(f"a: ${a}, b: ${b}, c: ${c}, d: ${d}")
  }

  case class MyServer(socket: Socket) extends Server(socket) {
    val serverHandshake = this <<< ImHereMessage >> SomeDataHere("hi", 4) >> SomeDataHere("lol", 5)
    serverHandshake()
  }

  val masterServer = new MasterServer(1111, MyServer.apply)
  val client = new MyClient("localhost", 1111)
}
