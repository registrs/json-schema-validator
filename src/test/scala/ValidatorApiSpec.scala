import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import com.validation.Main.transactor
import com.validation.domain.{ActionResult, SchemaContent, SchemaId}
import com.validation.{JsonValidationRepository, JsonValidationRepositoryImpl, JsonValidationService}
import doobie.{ExecutionContexts, FC, Transactor}
import doobie.hikari.HikariTransactor
import doobie.implicits.toSqlInterpolator
import io.circe.syntax.EncoderOps
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers
import com.validation.helpers.JsonCodecs._
import com.validation.domain.Action.{UploadSchema, ValidateDocument}
import com.validation.domain.Status.{Error, Success}

class ValidatorApiSpec extends AnyFunSpec with Matchers {

  val uploadedSchema: String =
    """{
      |  "not" : {
      |    "type" : [ "integer", "boolean" ]
      |  }
      |}""".stripMargin

  val invalidJson: SchemaContent = SchemaContent(1.asJson)
  val validJson: SchemaContent = SchemaContent("Foo".asJson)

  val fail: JsonValidationRepository[IO] = new JsonValidationRepository[IO] {
    def getSchema(id: SchemaId): IO[Option[String]] = IO.pure(None)

    def saveSchema(id: SchemaId, schemaContent: SchemaContent): IO[Int] = IO.raiseError(new Throwable("some error"))
  }

  val success: JsonValidationRepository[IO] = new JsonValidationRepository[IO] {
    def getSchema(id: SchemaId): IO[Option[String]] = IO.pure(Option(uploadedSchema))

    def saveSchema(id: SchemaId, schemaContent: SchemaContent): IO[Int] = IO.pure(1)
  }

  describe("get schema request") {
    it("should return schema on successful request") {

      val service = new JsonValidationService(success)

      val result = service.getSchema(SchemaId("1")).unsafeRunSync()
      result mustBe Some(uploadedSchema)
    }
  }

  describe("save schema request") {
    it("should notify about successful schema addition") {

      val service = new JsonValidationService(success)

      val result: ActionResult = service.saveSchema(SchemaId("1"), validJson).unsafeRunSync()
      result mustBe ActionResult(UploadSchema, SchemaId("1"), Success, None)
    }

    it("should notify about failed schema addition") {

      val service = new JsonValidationService(fail)

      val result: ActionResult = service.saveSchema(SchemaId("1"), validJson).unsafeRunSync()

      result mustBe ActionResult(UploadSchema, SchemaId("1"), Error, Some("some error"))
    }
  }

  describe("validate schema request") {
    it("should notify about successful schema validation") {

      val service = new JsonValidationService(success)

      val result: ActionResult = service.validateSchema(SchemaId("1"), validJson.content).unsafeRunSync()
      result mustBe ActionResult(ValidateDocument, SchemaId("1"), Success, None)
    }

    it("should notify about failed schema validation") {

      val service = new JsonValidationService(success)

      val result: ActionResult = service.validateSchema(SchemaId("1"), invalidJson.content).unsafeRunSync()
      result mustBe ActionResult(ValidateDocument, SchemaId("1"), Error, Some("instance matched a schema which it should not have"))
    }

    it("should notify about failed schema validation because of not found schema in db") {

      val service = new JsonValidationService(fail)

      val result: ActionResult = service.validateSchema(SchemaId("1"), validJson.content).unsafeRunSync()

      result mustBe ActionResult(ValidateDocument, SchemaId("1"), Error, Some("Schema was not found"))
    }
  }

}
