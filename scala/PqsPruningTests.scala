import scala.jdk.CollectionConverters.*
import com.daml.ledger.javaapi.data.CreatedEvent
import scala.sys.process.Process
import seed.NamedBlob

object PqsPruningTests:
  def init(): PqsPruningTests =
    val (admin, ledger) = initLedger("localhost", 6865)
    val pqs = PostgresClient("pqs")
    PqsPruningTests(admin, ledger, pqs)

  @main def pruning_test =
    init().test1()
    
class PqsPruningTests(admin: LedgerAdmin, ledger: PartyLedger, pqs: PostgresClient):
  def test1() =
    val b1 = createBlob("b1")
    val b2 = createBlob("b2")
    val b3 = createBlob("b3")
    archiveBlob(b1)
    val b4 = createBlob("b4")
    val b5 = createBlob("b5")
    archiveBlob(b3)
    archiveBlob(b4)
    val b6 = createBlob("b6")
    val b7 = createBlob("b7")
    val b8 = createBlob("b8")
    archiveBlob(b6)
    val b9 = createBlob("b9")
    runPqs(ledgerStart = "Genesis")
    val o1 = getOffset(b1)
    val o5 = getOffset(b5)
    val o7 = getOffset(b7)
    println(o1)
    println(o5)
    println(o7)
    prunePqs(o7)
    runPqs(ledgerStart = 8.toString, ledgerStop = o5.toString)

  def createBlob(name: String): NamedBlob.ContractId =
    ledger.submitAndWaitForTransaction(NamedBlob.create(name, ledger.party)).getTransaction.getEvents.asScala
      .collectFirst { case e: CreatedEvent => NamedBlob.ContractId(e.getContractId) }
      .get
  
  def archiveBlob(contractId: NamedBlob.ContractId) =
    ledger.submitAndWaitForTransaction(contractId.exerciseArchive)

  def getOffset(contractId: NamedBlob.ContractId): Long =
    pqs.getOffset(contractId.contractId)

  def runPqs(ledgerStart: String = "Genesis", ledgerStop: String = "Latest"): Int =
    val ledgerConfig = Seq("--source-ledger-host", "localhost", "--source-ledger-port", "6865")
    val postgresConfig = Seq("--target-postgres-host", "localhost", "--target-postgres-port", "5432", "--target-postgres-database", "pqs", "--target-postgres-username", "canton-admin", "--target-postgres-password", "canton-admin")
    val cmd = Seq("dpm", "pqs", "pipeline", "ledger", "postgres-document")
    val startAndStop = Seq("--pipeline-ledger-start", ledgerStart, "--pipeline-ledger-stop", ledgerStop)
    Process(cmd ++ ledgerConfig ++ postgresConfig ++ startAndStop).!

  def prunePqs(offset: Long): Int =
    pqs.prune(offset)
