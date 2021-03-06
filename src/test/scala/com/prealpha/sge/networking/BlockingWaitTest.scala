package com.prealpha.sge.networking

import com.prealpha.sge.logic.{Time, FrameRate}
import server.ToUserConnection
import com.prealpha.sge.messages.{ReceivedMessage, HandshakeMessage, Message}
import com.prealpha.sge.logging.log
import com.prealpha.sge.logic.game.{ServerGame, ClientGame}

case class HiMessage() extends Message {
  val frame = new Time(1, 1)

  override def toString = "HI MESSAGE"
}

case object HSMessage extends HandshakeMessage

object BlockingWaitTest extends App {

  class Sc extends ServerGame {
    def update(deltaT: Long) {}

    def init() {}

    def stepPhysics(deltaT: Long) {}

    def acceptUser(toUser: ToUserConnection): Boolean = {
      log.info("SERVER: called acceptUser")
      toUser.write(new HiMessage)
      log.info("SERVER: called write")
      toUser.flush()
      toUser.messagePublisher.blockUntilObject(ReceivedMessage)
      toUser.write(HSMessage)
      toUser.messagePublisher.blockUntilObject(ReceivedMessage)
      log.info("SERVER: sent HiMessage")
      true
    }

    implicit val framerate: FrameRate = FrameRate(300)
  }

  class Cc extends ClientGame("localhost") {
    //        val initM = this.toServer.messagePublisher.blockUntil(_.isInstanceOf[HiMessage])
    //        println(initM)

    def init() {
      val initM = this.toServer.messagePublisher.blockUntil(_ => true)
      println(initM)
      this.toServer.write(ReceivedMessage)
      val tt = this.toServer.messagePublisher.blockUntilObject(HSMessage)
      println(tt)
      this.toServer.write(ReceivedMessage)
    }

    def update(deltaT: Long) {}

    def stepPhysics(deltaT: Long) {}

    def render() {}

    implicit val framerate: FrameRate = FrameRate(300)
  }


  val server = new Sc
  server.startConnectionPool()

  val client = new Cc
  client.startConnection()
  client.init()
}
