import java.sql.*

@main def testScribe =
  val postgres = PostgresClient("pqs")
  println(postgres.countContracts())
  val query = postgres.prepareTestQuery("update __contracts set created_at_ix = 0 where created_at_ix < ?;")
  println(query.executeUpdate(45))

object PostgresClient:
  def apply(database: String): PostgresClient =
    val conn = DriverManager.getConnection(s"jdbc:postgresql://localhost:5432/$database", "canton-admin", "canton-admin")
    new PostgresClient(conn)

class PostgresClient(conn: Connection):
  private val getTransactionQuery = conn.prepareStatement("SELECT created_at_ix FROM __contracts ORDER BY created_at_ix ASC LIMIT 1 OFFSET ?;")
  def getTransaction(contracts: Int): Int =
    getTransactionQuery.setInt(1, contracts)
    val res = getTransactionQuery.executeQuery()
    res.next()
    res.getInt(1)

  private val getOffsetFromContractId = conn.prepareStatement("SELECT created_at_offset FROM creates() WHERE contract_id=?;")
  def getOffset(contractId: String): Long =
    getOffsetFromContractId.setString(1, contractId)
    val res = getOffsetFromContractId.executeQuery()
    res.next()
    res.getLong(1)
    
  private val getOffsetQuery = conn.prepareStatement("""SELECT "offset" FROM __transactions where ix=?;""")
  def getOffset(tx: Int): Int =
    getOffsetQuery.setInt(1, tx)
    val res = getOffsetQuery.executeQuery()
    res.next()
    res.getInt(1)

  private val countContractsQuery = conn.prepareStatement("SELECT COUNT(*) FROM __contracts")
  def countContracts(): Int =
    val res = countContractsQuery.executeQuery()
    res.next()
    res.getInt(1)

  private val pruneQuery = conn.prepareStatement("SELECT deleted_contracts FROM prune_to_offset_selectively(?);")
  def prune(offset: Long): Int =
    pruneQuery.setLong(1, offset)
    val res = pruneQuery.executeQuery()
    res.next()
    res.getInt(1)

  def prepareTestQuery(query: String): TestQuery = TestQuery(conn.prepareStatement("BEGIN;\n" + query + "\nROLLBACK;"))

class TestQuery(stmt: PreparedStatement):
  def executeSelect(tx: Int): Long =
    stmt.setInt(1, tx)
    val start = System.currentTimeMillis
    stmt.execute()
    System.currentTimeMillis - start

  def executeUpdate(tx: Int): Long =
    stmt.setInt(1, tx)
    val start = System.currentTimeMillis
    stmt.executeUpdate()
    System.currentTimeMillis - start
