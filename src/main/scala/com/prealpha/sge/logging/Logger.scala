package com.prealpha.sge.logging

import java.io.PrintStream



class Logger(out: PrintStream) {
    private[this] def logAt(level: String)(i: Any){
        out.println(level+":  "+i.toString)
        out.flush()
    }

    val info  = logAt("INFO") _
    val warn  = logAt("WARN") _
    val error = logAt("ERROR") _
    def trace(e: Throwable) = {
        e.printStackTrace()
        System.err.println(System.currentTimeMillis())
        logAt("TRACE")(e.getClass.getCanonicalName + ": " + e.getMessage + " :: " +
            e.getStackTraceString.replace(System.lineSeparator(),"\\n"))
    }
}
