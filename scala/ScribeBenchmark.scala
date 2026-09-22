@main def scribe_benchmark =
  val postgres = PostgresClient("pqs")
  val activeQueries = Seq(
    "select * from active(?) WHERE signatories @> array['7aaa39b9-53a8-475d-bffe-c485a93ef700::1220bdc97081a26d4c3e4ee9c15ff87a57058da8c6e85792c11b66a74e9c09e0c54b'] AND contract_id='006223c49aa3d3479b570f085f58fab0a8781e859faf94d47a29972b6f353183b7ca121220496c04ae86deb6367c78781d13aa1af7990ce030d3c76aea4f776a071277e83b';",
    "select * from active2(?) WHERE signatories @> array['7aaa39b9-53a8-475d-bffe-c485a93ef700::1220bdc97081a26d4c3e4ee9c15ff87a57058da8c6e85792c11b66a74e9c09e0c54b'] AND contract_id='006223c49aa3d3479b570f085f58fab0a8781e859faf94d47a29972b6f353183b7ca121220496c04ae86deb6367c78781d13aa1af7990ce030d3c76aea4f776a071277e83b';",
  ).map(postgres.prepareTestQuery)
  val qnames = Seq(
    "pkg2:Model:BlobA",
    "pkg2:Model:BlobB",
    "pkg2:Model:BlobC",
    "pkg2:Model:BlobD",
    "pkg2:Model:BlobE",
    "pkg2:Model:BlobF",
    "pkg2:Model:BlobG",
    "pkg2:Model:BlobH",
    "pkg2:Model:Blob",
  )
  println("Active queries")
  for qname <- qnames do
    val res = activeQueries.map: query =>
      Thread.sleep(1000)
      1.to(100).map(_ => query.executeSelect(qname)).sum
    println(s"$qname\t${res.mkString("\t")}")
  
  val summaryQueries = Seq(
    "select * from summary_active(?)",
    "select * from summary_active2(?)"
  ).map(postgres.prepareTestQuery)
  println("Summary active queries")
  for offset <- 30000.to(180000, 30000) do
    val res = summaryQueries.map: query =>
      Thread.sleep(1000)
      query.executeSelect(offset)
    println(s"$offset:\t${res.mkString("\t")}")

  println("Summary lookup queries")
  val contractIds = postgres.listContractIds(1000)
  val lookupQuerySql = Seq(
    "select * from lookup_contract(?) WHERE payload_type::text='interface' LIMIT 2;",
    "select * from lookup_contract2(?) WHERE payload_type::text='interface' LIMIT 2;"
  )
  val lookupQueries = lookupQuerySql.map(postgres.prepareTestQuery)
  for (querySql, query) <- lookupQuerySql.zip(lookupQueries) do
    contractIds.foreach(query.executeSelect(_))
    val samples = contractIds.map(contractId => timedNanos(query.executeSelect(contractId)))
    printLookupStats(querySql, samples)
    



def timedNanos[A](body: => A): Long =
  val start = System.nanoTime()
  body
  System.nanoTime() - start

def millis(nanos: Long): Double = nanos / 1_000_000.0

def percentile(sortedSamples: Seq[Long], p: Double): Double =
  val index = math.ceil(p * sortedSamples.size).toInt - 1
  millis(sortedSamples(math.max(0, math.min(index, sortedSamples.size - 1))))

def standardDeviationMillis(samples: Seq[Long], averageMillis: Double): Double =
  val variance = samples.map(sample => math.pow(millis(sample) - averageMillis, 2)).sum / samples.size
  math.sqrt(variance)

def printLookupStats(name: String, samples: Seq[Long]): Unit =
  if samples.isEmpty then
    println(s"$name\tno samples")
  else
    val sortedSamples = samples.sorted
    val totalMillis = samples.sum / 1_000_000.0
    val averageMillis = totalMillis / samples.size
    val stddevMillis = standardDeviationMillis(samples, averageMillis)
    val throughput = samples.size / (totalMillis / 1000.0)
    val histogram = Seq(
      "<0.5ms" -> samples.count(_ < 500_000L),
      "0.5-1ms" -> samples.count(sample => sample >= 500_000L && sample < 1_000_000L),
      "1-2ms" -> samples.count(sample => sample >= 1_000_000L && sample < 2_000_000L),
      "2-5ms" -> samples.count(sample => sample >= 2_000_000L && sample < 5_000_000L),
      ">=5ms" -> samples.count(_ >= 5_000_000L)
    ).map((bucket, count) => s"$bucket=$count").mkString(" ")

    println(
      f"$name count=${samples.size}%d total=${totalMillis}%.1fms rate=${throughput}%.1f/s " +
        f"avg=${averageMillis}%.3fms stddev=${stddevMillis}%.3fms min=${millis(sortedSamples.head)}%.3fms " +
        f"p50=${percentile(sortedSamples, 0.50)}%.3fms p90=${percentile(sortedSamples, 0.90)}%.3fms " +
        f"p95=${percentile(sortedSamples, 0.95)}%.3fms p99=${percentile(sortedSamples, 0.99)}%.3fms " +
        f"max=${millis(sortedSamples.last)}%.3fms $histogram"
    )
