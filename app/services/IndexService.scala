package services

import models.Index
import scala.collection.mutable.ListBuffer

case class IndexTree(name: String, id: String, children: ListBuffer[IndexTree] = ListBuffer.empty)

object IndexService {

  def buildIndexTree(indexs: Seq[Index]): IndexTree = {
    implicit val root = IndexTree(indexs.head.name, "")
    indexs.tail.foreach {
      node =>
        val parent = findParent(node).getOrElse(root)
        parent.children.append(IndexTree(node.name+":"+node.id, nodeKey(node)))
    }
    root
  }

  private def findParent(node: Index)(implicit tree: IndexTree): Option[IndexTree] = {

    if(tree.id == node.dbcode+node.pId) {
      Some(tree)
    } else if(!tree.children.isEmpty) {
      tree.children.map(t => findParent(node)(t)).filter(_.isDefined).flatten.headOption
    } else None

  }

  private def nodeKey(node: Index): String = node.dbcode + node.id

}
