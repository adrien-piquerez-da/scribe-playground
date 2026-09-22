import com.daml.ledger.api.v2.CommandCompletionServiceGrpc
import com.daml.ledger.api.v2.CommandCompletionServiceGrpc.CommandCompletionServiceBlockingStub
import com.daml.ledger.api.v2.CommandServiceGrpc
import com.daml.ledger.api.v2.CommandServiceGrpc.CommandServiceBlockingStub
import com.daml.ledger.api.v2.CommandSubmissionServiceGrpc
import com.daml.ledger.api.v2.CommandSubmissionServiceGrpc.CommandSubmissionServiceBlockingStub
import com.daml.ledger.api.v2.PackageServiceGrpc
import com.daml.ledger.api.v2.PackageServiceGrpc.PackageServiceBlockingStub
import com.daml.ledger.api.v2.PackageServiceOuterClass.ListVettedPackagesRequest
import com.daml.ledger.api.v2.StateServiceGrpc
import com.daml.ledger.api.v2.StateServiceGrpc.StateServiceBlockingStub
import com.daml.ledger.api.v2.UpdateServiceGrpc
import com.daml.ledger.api.v2.UpdateServiceGrpc.UpdateServiceBlockingStub
import com.daml.ledger.api.v2.admin.PackageManagementServiceGrpc
import com.daml.ledger.api.v2.admin.PackageManagementServiceGrpc.PackageManagementServiceBlockingStub
import com.daml.ledger.api.v2.admin.PackageManagementServiceOuterClass.UploadDarFileRequest
import com.daml.ledger.api.v2.admin.PartyManagementServiceGrpc
import com.daml.ledger.api.v2.admin.PartyManagementServiceGrpc.PartyManagementServiceBlockingStub
import com.daml.ledger.api.v2.admin.PartyManagementServiceOuterClass.*
import com.daml.ledger.api.v2.admin.UserManagementServiceGrpc
import com.daml.ledger.api.v2.admin.UserManagementServiceGrpc.UserManagementServiceBlockingStub
import com.daml.ledger.api.v2.admin.UserManagementServiceOuterClass.ListUsersRequest
import com.daml.ledger.javaapi.data.codegen.*
import com.daml.ledger.javaapi.data.{ContractId as _, Unit as _, ListUsersRequest as _, *}
import com.google.protobuf.ByteString
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import model.Blob
import model.Seed

import java.nio.file.Files
import java.nio.file.Path
import java.util as ju
import java.util.Optional
import java.util.UUID
import scala.jdk.CollectionConverters.*
import com.daml.ledger.api.v2.admin.ParticipantPruningServiceGrpc.ParticipantPruningServiceBlockingStub
import com.daml.ledger.api.v2.admin.ParticipantPruningServiceGrpc
import com.daml.ledger.api.v2.admin.ParticipantPruningServiceOuterClass.PruneRequest

@main def test: Unit =
  val (_, ledger) = initLedger("0.0.0.0", 6865)
    val seed = ledger.submitAndWaitForTransaction(Seed.create(ledger.party)).getTransaction.getEvents.asScala
      .collectFirst { case e: CreatedEvent => Seed.ContractId(e.getContractId) }
      .get
    val blobs = ledger.submitAndWaitForTransaction(seed.exerciseCreateMany(1,1,1,1,1,1,1,1)).getTransaction.getEvents.asScala
      .collect { case e: CreatedEvent => Blob.ContractId(e.getContractId) }
      .toSeq
    ledger.submitAndWaitForTransaction(seed.exerciseArchiveMany(blobs.asJava)).getTransaction.getEvents.asScala
      .foreach(println)

def initLedger(host: String, port: Int): (LedgerAdmin, PartyLedger) =
  val admin = LedgerAdmin(host, port)
  val userId = admin.listUsers.head
  println(userId)
  val partyId = UUID.randomUUID().toString
  val party = admin.createParty(partyId, userId)
  // admin.listKnownParties.getPartyDetailsList().asScala.headOption.get.getParty
  val syncs = admin.getConnectedSynchronizersRequest(party).getConnectedSynchronizersList.asScala
  val pkgs = admin.listVettedPackages.getVettedPackagesList.asScala.flatMap(_.getPackagesList.asScala)
  if pkgs.exists(_.getPackageName == "pkg2") then println("ok")
  else
    println("uploading dar")
    val synchronizerId = syncs.find(_.getSynchronizerAlias == "mysynchronizer").get.getSynchronizerId
    admin.uploadDar(Path.of(".daml/dist/pkg2-1.0.0.dar"), synchronizerId)
  (admin, PartyLedger(host, port, userId, party = party))
  
object LedgerAdmin:
  def apply(host: String, port: Int): LedgerAdmin =
    val channel = NettyChannelBuilder.forAddress(host, port).usePlaintext.build()
    new LedgerAdmin(
      UserManagementServiceGrpc.newBlockingStub(channel),
      PartyManagementServiceGrpc.newBlockingStub(channel),
      PackageManagementServiceGrpc.newBlockingStub(channel),
      PackageServiceGrpc.newBlockingStub(channel),
      ParticipantPruningServiceGrpc.newBlockingStub(channel),
      StateServiceGrpc.newBlockingStub(channel)
    )

class LedgerAdmin(
  userManagementService: UserManagementServiceBlockingStub,
  partyManagementService: PartyManagementServiceBlockingStub,
  packageManagementService: PackageManagementServiceBlockingStub,
  packageService: PackageServiceBlockingStub,
  pruningService: ParticipantPruningServiceBlockingStub,
  stateService: StateServiceBlockingStub,
):
  def listUsers =
    val request = ListUsersRequest.getDefaultInstance()
    userManagementService.listUsers(request).getUsersList().asScala.map(_.getId)

  def listKnownParties =
    val request = ListKnownPartiesRequest.getDefaultInstance()
    partyManagementService.listKnownParties(request)

  def createParty(hint: String, userId: String): String =
    val request = AllocatePartyRequest.newBuilder().setPartyIdHint(hint).setUserId(userId).build()
    val response = partyManagementService.allocateParty(request)
    response.getPartyDetails.getParty

  def listVettedPackages =
    val request = ListVettedPackagesRequest.getDefaultInstance()
    packageService.listVettedPackages(request)

  def getConnectedSynchronizersRequest(party: String) =
    val request = GetConnectedSynchronizersRequest(party)
    stateService.getConnectedSynchronizers(request.toProto)

  def uploadDar(pkg: Path, synchronizerId: String): Unit =
    val bytes = ByteString.copyFrom(Files.readAllBytes(pkg))
    val request = UploadDarFileRequest.newBuilder().setDarFile(bytes).setSynchronizerId(synchronizerId).build()
    packageManagementService.uploadDarFile(request)

  def prune(offset: Long): Unit =
    val request = PruneRequest.newBuilder().setPruneUpTo(offset).build()
    pruningService.prune(request)

object PartyLedger:
  def apply(host: String, port: Int, userId: String, party: String): PartyLedger =
    val channel = NettyChannelBuilder.forAddress(host, port).usePlaintext.build()
    new PartyLedger(
      CommandSubmissionServiceGrpc.newBlockingStub(channel),
      CommandServiceGrpc.newBlockingStub(channel),
      CommandCompletionServiceGrpc.newBlockingStub(channel),
      UpdateServiceGrpc.newBlockingStub(channel),
      userId,
      party
    )

class PartyLedger(
  submissionService: CommandSubmissionServiceBlockingStub,
  commandService: CommandServiceBlockingStub,
  completionService: CommandCompletionServiceBlockingStub,
  updateService: UpdateServiceBlockingStub,
  val userId: String,
  val party: String
):
  private val eventFormat = EventFormat(
    ju.Map.of(party, new CumulativeFilter(ju.Map.of(), ju.Map.of(), Optional.of(Filter.Wildcard.HIDE_CREATED_EVENT_BLOB))),
    Optional.empty(),
    false
  )
  private val transactionFormat = TransactionFormat(eventFormat, TransactionShape.ACS_DELTA)

  def submitAndWaitForTransaction[T](update: Update[T]): SubmitAndWaitForTransactionResponse =    
    val request = SubmitAndWaitForTransactionRequest(update.submission)
    SubmitAndWaitForTransactionResponse.fromProto(commandService.submitAndWaitForTransaction(request.toProto))

  def submit[T](update: Update[T]): Unit =
    submissionService.submit(SubmitRequest.toProto(update.submission))

  def transactions(): Iterator[Transaction] =
    val updateFormat = UpdateFormat(Optional.of(transactionFormat), Optional.empty, Optional.empty)
    val request = GetUpdatesRequest(0L, Optional.empty, updateFormat)
    updateService.getUpdates(request.toProto).asScala
      .filter(_.hasTransaction)
      .map(t => Transaction.fromProto(t.getTransaction))

  extension [T] (update: Update[T])
    private def submission: CommandsSubmission =
      CommandsSubmission
        .create(userId, UUID.randomUUID().toString, Optional.empty, update.commands)
        .withActAs(party)
