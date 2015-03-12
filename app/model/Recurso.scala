package model

import reactivemongo.bson._

case class Recurso(
                    titulo: Option[String],
                    descripcion: Option[String],
                    imagen: Option[String],
                    url_recurso: Option[String],
                    url_descarga: Option[String],
                    apartados: List[Apartado]
                    )

case class Apartado(
                     titulo: Option[String],
                     subapartados: List[SubApartado]
                     )

case class SubApartado(
                        titulo: Option[String],
                        valores: List[String]
                        )


object Recurso {
  implicit object RecursoReader extends BSONDocumentReader[Recurso] {
    def read(doc: BSONDocument): Recurso = {
      val apartados = doc.getAs[BSONArray]("apartados").map { array =>
        array.values.map { apartado =>
          Apartado.ApartadoReader.read(apartado.seeAsOpt[BSONDocument].get)
        }
      }

//      println(apartados.get.toList)
      Recurso(
        doc.getAs[String]("titulo"),
        doc.getAs[String]("descripcion"),
        doc.getAs[String]("imagen"),
        doc.getAs[String]("url_recurso"),
        doc.getAs[String]("url_descarga"),
        apartados.get.toList
      )
    }
  }
}

object Apartado{
  implicit  object ApartadoReader extends BSONDocumentReader[Apartado]{
    def read(doc: BSONDocument): Apartado = {

      val subapartados = doc.getAs[BSONArray]("valor").map { array =>
        array.values.map { subapartado =>
          SubApartado.SubApartadoReader.read(subapartado.seeAsOpt[BSONDocument].get)
        }
      }
//      println(subapartados)
      Apartado(
        doc.getAs[String]("titulo"),
        subapartados.getOrElse(Stream()).toList
      )
    }
  }
}


object SubApartado{
  implicit object SubApartadoReader extends BSONDocumentReader[SubApartado]{
    def read(doc: BSONDocument): SubApartado = {

      try {
        val valores = if (doc.get("valor").get.isInstanceOf[BSONString]) Some(Stream(doc.getAs[String]("valor").get))
        else doc.getAs[BSONArray]("valor").map { array =>
          array.values.map { valor =>
            valor.seeAsOpt[String].get
          }
        }


      SubApartado(
        doc.getAs[String]("titulo"),
        valores.getOrElse(Stream()).toList
      )
      }catch {
        case e: NoSuchElementException => SubApartado(
          doc.getAs[String]("titulo"),
          List()
        )
      }
    }
  }
}
