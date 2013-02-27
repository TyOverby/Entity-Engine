package com.prealpha.sge.networking

import com.prealpha.sge.messages.{HandshakeMessage, Message}
import java.net.Socket

object HandshakeGenerator


private[this] sealed trait NetOp

private[this] case class OutgoingMessage(m: Message) extends NetOp

private[this] case class IncomingMessage[M <: Message, A](d: M => Option[A]) extends NetOp
private[this] case class NoCareIncoming[M <: Message, A](p: M => Option[A]) extends NetOp
private[this] case class NoCareObject[M <: Message](o: M) extends NetOp

object ConStarter {
  implicit def conn2ConStarter(con: AbstractConnectionThread): ConStarter = ConStarter(con)
}

case object RECMESSAGE extends HandshakeMessage

case class ConStarter(conn: AbstractConnectionThread) {
  private[this] var ops = List[NetOp]()

  def >>(m: => Message) = {
    ops = OutgoingMessage(m) :: ops
    this
  }

  def <<[M <: Message, A](test: M => Option[A]) = {
    ops = IncomingMessage(test) :: ops
    this
  }
  def <<<[M <: Message, A](test: M => Option[A]) = {
    ops = NoCareIncoming(test) :: ops
    this
  }

  def <<<[M <: Message](obj: M) = {
    ops = NoCareObject(obj) :: ops
    this
  }

  def apply(): List[Any] = {
    def app(ops: List[NetOp]): List[Any] = {
      ops match {
        case Nil => Nil
        case OutgoingMessage(m) :: xs => {
          conn write m
          conn.messagePublisher.blockUntilObject(RECMESSAGE)
          app(xs)
        }
        case NoCareIncoming(d) :: xs => {
          conn.messagePublisher.blockUnapply(d)
          conn.write(RECMESSAGE)
          app(xs)
        }
        case NoCareObject(o) :: xs => {
          conn.messagePublisher.blockUntilObject(o)
          conn.write(RECMESSAGE)
          app(xs)
        }
        case IncomingMessage(d) :: xs => {
          val rec = conn.messagePublisher.blockUnapply(d)
          conn.write(RECMESSAGE)
          rec :: app(xs)
        }
      }
    }

    app(ops.reverse)
  }
}

object Test extends App {

  import ConStarter._

  case class DoSomeExtracting(str: String, int: Int) extends HandshakeMessage

  new AbstractConnectionThread(new Socket()) >> new HandshakeMessage {} >> new HandshakeMessage {} << DoSomeExtracting.unapply
}

//
//import com.prealpha.sge.messages._
//
//
//class NetOp(val prev: Option[Sequence] = None) {
//
//  def <<[M <: Message, A] (d: M => Option[A]) = {
//    new IncomingSequence(d)(this)
//  }
//
//  def >> (m: => Message) = {
//    new OutgoingSequence(m)(this)
//  }
//}
//
//
//trait StartignSequence extends Sequence{
//  def write(m: Message)
//  val messagePublisher: Publisher[Message]
//}
//
//class StartSeq(val connection: AbstractConnectionThread) extends Sequence(None) with StartignSequence{
//  def write(m: Message) {connection.write(m)}
//  val messagePublisher = connection.messagePublisher
//}
//case object FakeStart extends Sequence(None) with StartignSequence{
//  def write(m: Message) {println("")}
//  val messagePublisher = new Publisher[Message]()
//}
//class OutgoingSequence(val m: Message)(p: Sequence) extends Sequence(Some(p))
//class IncomingSequence[M <: Message, A](val d: M => Option[A])(p: Sequence) extends Sequence(Some(p))
//
//
//object HandshakeGenerator {
//  implicit def act2seq(con: AbstractConnectionThread): StartSeq = new StartSeq(con)
//
//  def reverse(s: Sequence): List[Sequence] = {
//    def rev(s: Sequence, b: List[Sequence]): List[Sequence] = {
//      s match{
//        case s: StartSeq      => b
//        case FakeStart                => b
//        case s: OutgoingSequence      => rev(s.prev.get, s::b)
//        case s: IncomingSequence[_,_] => rev(s.prev.get, s::b)
//      }
//    }
//    rev(s, List[Sequence]())
//  }
//
//  def connection(s: Sequence): Sequence = {
//    reverse(s).head.prev.get
//  }
//
//  def apply(connection: StartSeq, cons: List[Sequence]): List[_] = {
//    cons match {
//      case Nil => List()
//      case (out: OutgoingSequence) :: xs => connection.write(out.m); apply(connection, xs)
//      case (in: IncomingSequence[_,_])  :: xs => connection.messagePublisher.blockUnapply(in.d) :: apply(connection,xs)
//    }
//  }
//}
//
//object Tester extends App{
//  import HandshakeGenerator._
//
//  val x = FakeStart << SyncMessage.unapply >> SyncMessage(null, null)
//  println(reverse(x))
//  println(connection(x))
//  println(x.prev)
//}
