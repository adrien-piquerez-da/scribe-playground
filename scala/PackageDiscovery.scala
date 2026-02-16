import scala.sys.process.Process
import scala.jdk.CollectionConverters.*
import java.nio.file.Path
import scala.util.control.NonFatal
import foo.Foo
import bar.Bar

object PackageDiscovery:
  @main def package_discovery: Unit =
    docker("compose", "up", "-d")
    val ledger = init().test1()

  def init(host: String = "localhost", port: Int = 6865) =
    val admin = LedgerAdmin(host, port)
    val party = retry(10)(admin.listKnownParties.getPartyDetailsList.asScala.head).getParty
    val ledger = PartyLedger(host, port, userId = "default", party = party)
    val syncs = admin.getConnectedSynchronizersRequest(party).getConnectedSynchronizersList.asScala
    val syncId = syncs.find(_.getSynchronizerAlias == "mysynchronizer").get.getSynchronizerId
    val postgres = PostgresClient("pqs")
    new PackageDiscovery(admin, ledger, postgres, syncId)

  def docker(args: String*) = Process(Seq("docker") ++ args).!

  def retry[T](n: Int)(f: => T): T =
    try f
    catch case NonFatal(cause) if n > 0 =>
      println(s"retrying: ${cause.getMessage}")
      Thread.sleep(2000)
      retry(n - 1)(f)

class PackageDiscovery(admin: LedgerAdmin, ledger: PartyLedger, postgres: PostgresClient, syncId: String):
  val fooDar = Path.of("./daml/foo/.daml/dist/foo-1.0.0.dar")
  val barDar = Path.of("./daml/bar/.daml/dist/bar-1.0.0.dar")
  def test1() =
    admin.uploadDar(fooDar, syncId)
    val foo1 = ledger.submitAndWaitForTransaction(Foo.create("foo1", ledger.party))
    val foo2 = ledger.submitAndWaitForTransaction(Foo.create("foo2", ledger.party))
    val pqs1 = Pqs().start()
    Thread.sleep(5000) // let PQS initialize itself
    val foo3 = ledger.submitAndWaitForTransaction(Foo.create("foo3", ledger.party))
    admin.uploadDar(barDar, syncId)
    val bar1 = ledger.submitAndWaitForTransaction(Bar.create("bar1", ledger.party))
    val foo4 = ledger.submitAndWaitForTransaction(Foo.create("foo4", ledger.party))
    val bar2 = ledger.submitAndWaitForTransaction(Bar.create("bar2", ledger.party))
    Thread.sleep(5000) // let PQS catch up
    pqs1.destroy()
    postgres.listContractNames().foreach(println)
    val pqs2 = Pqs().start()
    Thread.sleep(5000) // let PQS catch up
    postgres.listContractNames().foreach(println)
    // System.console().readLine()

