package com.prealpha.sge.entity

import com.prealpha.sge.messages.Message
import com.prealpha.sge.logic.Frame

trait HelloComponent extends Component{
    var word = "hello"

    abstract override
    def update(deltaM: Long){
        println(word)

        super.update(deltaM)
    }

    def handle(m: HelloMessage){
        this.word = m.newWord
    }
}

case class HelloMessage(newWord: String, frame: Frame) extends Message
