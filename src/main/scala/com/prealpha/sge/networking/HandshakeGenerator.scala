package com.prealpha.sge.networking

import com.prealpha.sge.messages._


class Sequence(val prev: Option[Sequence] = None) {

  def <<[M <: Message, A] (d: M => Option[A]) = {
    new IncomingSequence(d)(this)
  }

  def >> (m: => Message) = {
    new OutgoingSequence(m)(this)
  }
}



class StartingSequence(val connection: AbstractConnectionThread) extends Sequence(None)
case object FakeStart extends Sequence(None)
class OutgoingSequence(val m: Message)(p: Sequence) extends Sequence(Some(p))
class IncomingSequence[M <: Message, A](val d: M => Option[A])(p: Sequence) extends Sequence(Some(p))


object HandshakeGenerator {
  implicit def act2seq(con: AbstractConnectionThread): StartingSequence = new StartingSequence(con)

  def reverse(s: Sequence) = {
    def rev(s: Sequence, b: List[Sequence]): List[Sequence] = {
      s match{
        case s: StartingSequence      => b
        case FakeStart                => b
        case s: OutgoingSequence      => rev(s.prev.get, s::b)
        case s: IncomingSequence[_,_] => rev(s.prev.get, s::b)
      }
    }
    rev(s, List[Sequence]())
  }
}

object Tester extends App{
  import HandshakeGenerator._

  val x = FakeStart << SyncMessage.unapply >> SyncMessage(null,null)
  println(reverse(x))
  println(x.prev)
}
