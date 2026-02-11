# Scribe Playground

* Use docker compose to start PostgreSQL, Canton and PQS (Scribe).

```sh
docker compose up
```

* Build the daml package:

```sh
dpm build
```

* Start the `populate_ledger` script:

```sh
scala run . --main-class populate_ledger
```

The `populate_ledger` script runs indefinitely. You can stop it manually as soon as you have enough transactions and/or contracts in the database.

* Start the `scribe_benchmark` script:

```sh
scala run . --main-class scribe_benchmark
```
