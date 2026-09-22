
import com.daml.ledger.javaapi.data.{ Unit as _, ContractId as _, * }
import model.Blob
import model.Seed

import java.util as ju
import scala.jdk.CollectionConverters.*
import scala.util.Random
import ju.concurrent.atomic.AtomicLong
import io.grpc.StatusRuntimeException
import ju.concurrent.atomic.AtomicReference

object PopulateLedger:
  val buckets = Array.fill(4)(AtomicReference(Seq.empty[Blob.ContractId]))
  val activeBlobCounts = AtomicLong(0)
  val archivedBlobCounts = AtomicLong(0)
  val random = Random()

  @main def populate_ledger: Unit =
    val (_, ledger) = initLedger("localhost", 6865)
    Listener(ledger.transactions()).start()
    Archivist(ledger).start()
    Creator(ledger).run()

  def getRandomBucket() = buckets(random.nextInt(buckets.length))

  def creationDelay = 1000 / 20
  def deletionDelay = 1000 / 2

  class Listener(transactions: Iterator[Transaction]) extends Thread:
    override def run(): Unit =
      var startTime = System.currentTimeMillis()
      var count = 0
      for transaction <- transactions
      do
        val creates = transaction.getEvents.asScala
          .collect { case e: CreatedEvent if e.getTemplateId.isBlob => Blob.ContractId(e.getContractId) }
          .toSeq
        val archives = transaction.getEvents.asScala
          .collect { case e: ArchivedEvent if e.getTemplateId.isBlob => Blob.ContractId(e.getContractId) }
          .toSeq
        getRandomBucket().updateAndGet(prev => random.shuffle(prev ++ creates))
        val activeBlobs = activeBlobCounts.addAndGet(creates.size - archives.size)
        val archivedBlobs = archivedBlobCounts.addAndGet(archives.size)
        count += 1
        if count % 200 == 0 then
          val speed = count * 1000 / (System.currentTimeMillis() - startTime)
          println(s"$speed tx/sec\t${activeBlobs} active blobs\t${archivedBlobs} archived blobs") // + "\t${1000/archivingDelay} archive/sec")
          count = 0
          startTime = System.currentTimeMillis()

  class Creator(ledger: PartyLedger) extends Thread:
    override def run(): Unit =
      val seed = ledger.submitAndWaitForTransaction(Seed.create(ledger.party)).getTransaction.getEvents.asScala
        .collectFirst { case e: CreatedEvent => Seed.ContractId(e.getContractId) }
        .get
      while true do
        ledger.submit(seed.exerciseCreateMany(random.nextLong(2), random.nextLong(4), random.nextLong(6), random.nextLong(8), random.nextLong(10), random.nextLong(12), random.nextLong(14), random.nextLong(16)))
        Thread.sleep(creationDelay)

  class Archivist(ledger: PartyLedger) extends Thread:
    override def run(): Unit =
      val seed = ledger.submitAndWaitForTransaction(Seed.create(ledger.party)).getTransaction.getEvents.asScala
          .collectFirst { case e: CreatedEvent => Seed.ContractId(e.getContractId) }
          .get
      while true do
        val activeBlobs = activeBlobCounts.get
        val archivedBlobs = archivedBlobCounts.get
        val size = ((archivedBlobs + activeBlobs) * 3 / 4 - archivedBlobs).toInt
        if size > 0 then
          val blobs = getRandomBucket().getAndUpdate(_.drop(size)).take(size)
          try 
            ledger.submit(seed.exerciseArchiveMany(blobs.asJava))
            Thread.sleep(deletionDelay)
          catch case e: StatusRuntimeException => ()
        else Thread.sleep(deletionDelay)

  extension (id: Identifier) private def isBlob: Boolean = id.getEntityName.startsWith("Blob")
