
@main def scribe_benchmark =
  val postgres = PostgresClient("pqs")
  val testQueries = Seq(
    "select * from prune_to_offset(?);",
    "select * from prune_to_offset_selectively(?);",
  ).map(postgres.prepareTestQuery)
  for tx <- 10000.to(100000, 10000) do
    val offset = postgres.getOffset(tx)
    val res = testQueries.map: q =>
      Thread.sleep(1000) // let postgres breath
      q.executeSelect(tx)
    println(s"$tx\t${res.mkString("\t")}")
