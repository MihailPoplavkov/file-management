package model

import java.time.Instant

case class Link(id: Option[Long] = None, fileId: String, expired: Instant)
