package utils

import java.time.Instant

import slick.ast.BaseTypedType
import slick.jdbc.{JdbcType, PostgresProfile}

trait DbProfile extends PostgresProfile {

  trait AdditionalImplicits extends API {
    implicit val instant2String: JdbcType[Instant] with BaseTypedType[Instant] =
      MappedColumnType.base[Instant, String](_.toString, Instant.parse)
  }

  override val api = new API with AdditionalImplicits
}

object DbProfile extends DbProfile
