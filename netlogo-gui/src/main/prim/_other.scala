// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.prim

import org.nlogo.agent.{Agent, AgentSet, LazyAgentSet}
import org.nlogo.api.Dump
import org.nlogo.core.I18N
import org.nlogo.nvm.{Context, Reporter}
import org.nlogo.nvm.RuntimePrimitiveException

class _other extends Reporter {

  def report(context: Context): AgentSet = {
    val sourceSet = argEvalAgentSet(context, 0)
    
    if (sourceSet.isInstanceOf[LazyAgentSet]) {
      sourceSet.asInstanceOf[LazyAgentSet].lazyOther(context.agent)
      sourceSet
    } else {
      new LazyAgentSet(sourceSet.kind, null, sourceSet, others = List(context.agent))
    }
  }

}