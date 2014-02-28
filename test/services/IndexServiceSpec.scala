package services

import models.Index
import org.specs2.mutable.Specification
import scala.collection.mutable.ListBuffer

class IndexServiceSpec extends Specification {

  "build tree from" should {
    "only leaf" in {
        val leaves = List(
          Index("fsjdks","A05","1","价格指数",true,Some(0),None),
          Index("fsjdks","A0501","A05","农产品生产价格指数",false,Some(4),None),
          Index("fsjdks","A050105","A0501","渔业产品生产价格指数_当季值",true,Some(1),None),
          Index("fsjdks","A050102","A0501","种植业产品生产价格指数_当季值",true,Some(1),None),
          Index("fsjdks","A050101","A0501","农产品生产价格指数_当季值",true,Some(1),None)

        )

      val tree = IndexService.buildIndexTree(leaves)
      val root = IndexTree("价格指数","",ListBuffer(IndexTree("农产品生产价格指数:A0501","fsjdksA0501",ListBuffer(IndexTree("渔业产品生产价格指数_当季值:A050105","fsjdksA050105",ListBuffer()), IndexTree("种植业产品生产价格指数_当季值:A050102","fsjdksA050102",ListBuffer()), IndexTree("农产品生产价格指数_当季值:A050101","fsjdksA050101",ListBuffer())))))
      tree === root

    }

  }
}
