import scala.sys.process.Process

class Pqs(ledgerStart: Option[String] = None, ledgerStop: Option[String] = None):
  def run(): Int = process.!
  def start(): Process = process.run()

  private def process =
    val ledgerConfig = Seq("--source-ledger-host", "localhost", "--source-ledger-port", "6865")
    val postgresConfig = Seq("--target-postgres-host", "localhost", "--target-postgres-port", "5432", "--target-postgres-database", "pqs", "--target-postgres-username", "canton-admin", "--target-postgres-password", "canton-admin")
    val cmd = Seq("dpm", "pqs", "pipeline", "ledger", "postgres-document")
    val startAndStop = ledgerStart.toArg("--pipeline-ledger-start") ++ ledgerStop.toArg("--pipeline-ledger-stop")
    Process(cmd ++ ledgerConfig ++ postgresConfig ++ startAndStop)

  extension (value: Option[String])
    def toArg(name: String): Seq[String] =
      value match
        case Some(value) => Seq(name, value)
        case None => Seq.empty
