-- Active: 1770020971058@@127.0.0.1@5432@pqs

SELECT SUM(count) FROM summary_creates();

create or replace function active2(
    qname text default null,
    "offset" __transactions."offset"%type default latest_offset()
) returns setof contract
as
$$
  SELECT DISTINCT ON (c.template_fqn, c.contract_id) c.*
  FROM __contracts(qname) c
  WHERE c.life_ix @> (SELECT __nearest_ix_floor("offset")) AND NOT c.divulged_only
  ORDER BY c.template_fqn, c.contract_id, c.created_at_offset
$$ language sql stable parallel safe;

create or replace function active3(
    qname text default null,
    "offset" __transactions."offset"%type default latest_offset()
) returns setof contract
as
$$
  SELECT c.*
  FROM __contracts(qname) c
  JOIN (
    SELECT DISTINCT ON (contract_id) contract_id, created_at_ix
    FROM __contracts  -- or whatever narrows to the same partition
    WHERE life_ix @> (SELECT __nearest_ix_floor("offset"))
      AND NOT divulged_only
      AND (qname is null OR tpe_pk = (SELECT __contract_tpe4name(qname)))
    ORDER BY contract_id, created_at_ix DESC
  ) USING (contract_id, created_at_ix)
$$ language sql stable parallel safe;


create or replace function active4(
    qname text default null,
    "offset" __transactions."offset"%type default latest_offset()
) returns setof contract
as
$$
  WITH selected AS (
    SELECT DISTINCT ON (c.contract_id) c.*
    FROM __contracts c
    WHERE c.life_ix @> (SELECT __nearest_ix_floor("offset"))
      AND NOT c.divulged_only
      AND (qname is null OR c.tpe_pk = __contract_tpe4name(qname))
    ORDER BY c.contract_id, c.created_at_ix DESC
  )
  select tpe.template_fqn,
       tpe.payload_type,
       c.create_event_pk,
       ce.event_id,
       c.created_at_ix,
       ct."offset",
       c.archive_event_pk,
       ae.event_id,
       c.archived_at_ix,
       at."offset",
       c.life_ix,
       c.contract_id,
       c.payload,
       c.contract_key,
       c.metadata,
       ct.effective_at,
       at.effective_at,
       c.redaction_id,
       p.name,
       p.version,
       p.id,
       c.signatories,
       c.observers,
       c.witnesses,
       c.divulged_only,
       -- Storage optimization and backward compatibility:
       -- We don't store the creation package id on the contract if it is the same as the representative package,
       -- which is the common case.
       -- It was also not stored before the creation_package_id column was added. In those cases, the package id
       -- was always the creation package id.
       COALESCE(c.creation_package_id, p.id) as creation_package_id,
       c.contract_key_hash
  from selected c
         left join __contract_tpe tpe on tpe.pk = c.tpe_pk
         left join __transactions ct on c.created_at_ix = ct.ix
         left join __transactions at on c.archived_at_ix = at.ix
         left join __events ce on ce.pk = c.create_event_pk
         left join __events ae on ae.pk = c.archive_event_pk
         left join __packages p on c.package_pk = p.pk
  where qname is null or c.tpe_pk = __contract_tpe4name(qname)
$$ language sql stable parallel safe;


SELECT * FROM active('pkg:Model:BlobA');

SELECT * FROM active2('pkg:Model:BlobA');

SELECT * FROM active3('pkg:Model:BlobA');

SELECT * FROM active4('pkg:Model:BlobA');


EXPLAIN ANALYZE SELECT * FROM active('pkg:Model:BlobA');
Nested Loop Left Join  (cost=1206.00..46269.47 rows=3566 width=899) (actual time=65.742..848.839 rows=477313 loops=1)
  InitPlan 1
    ->  Result  (cost=0.00..0.51 rows=1 width=8) (actual time=0.805..0.805 rows=1 loops=1)
  ->  Gather  (cost=1205.35..46216.22 rows=3566 width=871) (actual time=65.731..746.458 rows=477313 loops=1)
        Workers Planned: 2
        Workers Launched: 2
        ->  Hash Left Join  (cost=205.35..44859.62 rows=1486 width=871) (actual time=52.754..760.205 rows=159104 loops=3)
              Hash Cond: (c.package_pk = p.pk)
              ->  Nested Loop Left Join  (cost=181.62..44831.97 rows=1486 width=783) (actual time=52.643..739.500 rows=159104 loops=3)
                    ->  Nested Loop Left Join  (cost=181.19..33874.17 rows=1486 width=750) (actual time=52.636..705.211 rows=159104 loops=3)
                          ->  Nested Loop Left Join  (cost=180.76..22916.36 rows=1486 width=717) (actual time=52.616..426.927 rows=159104 loops=3)
                                ->  Nested Loop Left Join  (cost=180.34..17138.99 rows=1486 width=701) (actual time=52.612..393.418 rows=159104 loops=3)
                                      ->  Parallel Bitmap Heap Scan on __contracts_8 c  (cost=179.92..11361.63 rows=1486 width=685) (actual time=52.581..164.332 rows=159104 loops=3)
                                            Recheck Cond: ((life_ix @> (InitPlan 1).col1) AND (NOT divulged_only))
                                            Filter: (tpe_pk = '8'::bigint)
                                            Heap Blocks: exact=15728
                                            ->  Bitmap Index Scan on __contracts_8_life_ix_tpe_pk_idx  (cost=0.00..179.03 rows=3566 width=0) (actual time=58.305..58.305 rows=522271 loops=1)
                                                  Index Cond: (life_ix @> (InitPlan 1).col1)
                                      ->  Index Scan using __transactions_pkey on __transactions ct  (cost=0.42..3.89 rows=1 width=24) (actual time=0.001..0.001 rows=1 loops=477313)
                                            Index Cond: (ix = c.created_at_ix)
                                ->  Index Scan using __transactions_pkey on __transactions at  (cost=0.42..3.89 rows=1 width=24) (actual time=0.000..0.000 rows=0 loops=477313)
                                      Index Cond: (ix = c.archived_at_ix)
                          ->  Index Scan using __events_pkey on __events ce  (cost=0.43..7.37 rows=1 width=41) (actual time=0.002..0.002 rows=1 loops=477313)
                                Index Cond: (pk = c.create_event_pk)
                    ->  Index Scan using __events_pkey on __events ae  (cost=0.43..7.37 rows=1 width=41) (actual time=0.000..0.000 rows=0 loops=477313)
                          Index Cond: (pk = c.archive_event_pk)
              ->  Hash  (cost=16.10..16.10 rows=610 width=104) (actual time=0.012..0.012 rows=2 loops=3)
                    Buckets: 1024  Batches: 1  Memory Usage: 9kB
                    ->  Seq Scan on __packages p  (cost=0.00..16.10 rows=610 width=104) (actual time=0.009..0.009 rows=2 loops=3)
  ->  Materialize  (cost=0.15..8.17 rows=1 width=44) (actual time=0.000..0.000 rows=1 loops=477313)
        ->  Index Scan using __contract_tpe_pkey on __contract_tpe tpe  (cost=0.15..8.17 rows=1 width=44) (actual time=0.007..0.007 rows=1 loops=1)
              Index Cond: (pk = '8'::bigint)
Planning Time: 6.427 ms
Execution Time: 861.226 ms


EXPLAIN ANALYZE SELECT * FROM active2('pkg:Model:BlobA');

Unique  (cost=46479.87..46506.61 rows=200 width=899) (actual time=2142.446..2526.824 rows=477313 loops=1)
  InitPlan 1
    ->  Result  (cost=0.00..0.51 rows=1 width=8) (actual time=1.753..1.754 rows=1 loops=1)
  ->  Sort  (cost=46479.36..46488.27 rows=3566 width=899) (actual time=2142.444..2457.826 rows=477313 loops=1)
        Sort Key: tpe.template_fqn, c.contract_id, ct."offset"
        Sort Method: external merge  Disk: 342832kB
        ->  Nested Loop Left Join  (cost=1205.49..46268.96 rows=3566 width=899) (actual time=82.338..705.152 rows=477313 loops=1)
              ->  Gather  (cost=1205.35..46216.22 rows=3566 width=871) (actual time=82.325..602.593 rows=477313 loops=1)
                    Workers Planned: 2
                    Workers Launched: 2
                    ->  Hash Left Join  (cost=205.35..44859.62 rows=1486 width=871) (actual time=65.233..703.288 rows=159104 loops=3)
                          Hash Cond: (c.package_pk = p.pk)
                          ->  Nested Loop Left Join  (cost=181.62..44831.97 rows=1486 width=783) (actual time=65.117..682.141 rows=159104 loops=3)
                                ->  Nested Loop Left Join  (cost=181.19..33874.17 rows=1486 width=750) (actual time=65.111..647.917 rows=159104 loops=3)
                                      ->  Nested Loop Left Join  (cost=180.76..22916.36 rows=1486 width=717) (actual time=65.079..402.207 rows=159104 loops=3)
                                            ->  Nested Loop Left Join  (cost=180.34..17138.99 rows=1486 width=701) (actual time=65.074..368.186 rows=159104 loops=3)
                                                  ->  Parallel Bitmap Heap Scan on __contracts_8 c  (cost=179.92..11361.63 rows=1486 width=685) (actual time=65.039..175.521 rows=159104 loops=3)
                                                        Recheck Cond: ((life_ix @> (InitPlan 1).col1) AND (NOT divulged_only))
                                                        Filter: (tpe_pk = '8'::bigint)
                                                        Heap Blocks: exact=13035
                                                        ->  Bitmap Index Scan on __contracts_8_life_ix_tpe_pk_idx  (cost=0.00..179.03 rows=3566 width=0) (actual time=73.311..73.311 rows=522271 loops=1)
                                                              Index Cond: (life_ix @> (InitPlan 1).col1)
                                                  ->  Index Scan using __transactions_pkey on __transactions ct  (cost=0.42..3.89 rows=1 width=24) (actual time=0.001..0.001 rows=1 loops=477313)
                                                        Index Cond: (ix = c.created_at_ix)
                                            ->  Index Scan using __transactions_pkey on __transactions at  (cost=0.42..3.89 rows=1 width=24) (actual time=0.000..0.000 rows=0 loops=477313)
                                                  Index Cond: (ix = c.archived_at_ix)
                                      ->  Index Scan using __events_pkey on __events ce  (cost=0.43..7.37 rows=1 width=41) (actual time=0.001..0.001 rows=1 loops=477313)
                                            Index Cond: (pk = c.create_event_pk)
                                ->  Index Scan using __events_pkey on __events ae  (cost=0.43..7.37 rows=1 width=41) (actual time=0.000..0.000 rows=0 loops=477313)
                                      Index Cond: (pk = c.archive_event_pk)
                          ->  Hash  (cost=16.10..16.10 rows=610 width=104) (actual time=0.010..0.010 rows=2 loops=3)
                                Buckets: 1024  Batches: 1  Memory Usage: 9kB
                                ->  Seq Scan on __packages p  (cost=0.00..16.10 rows=610 width=104) (actual time=0.007..0.008 rows=2 loops=3)
              ->  Materialize  (cost=0.15..8.17 rows=1 width=44) (actual time=0.000..0.000 rows=1 loops=477313)
                    ->  Index Scan using __contract_tpe_pkey on __contract_tpe tpe  (cost=0.15..8.17 rows=1 width=44) (actual time=0.009..0.009 rows=1 loops=1)
                          Index Cond: (pk = '8'::bigint)
Planning Time: 4.686 ms
Execution Time: 2554.217 ms


ANALYZE __contracts_8;
EXPLAIN (ANALYZE, BUFFERS, VERBOSE, SETTINGS) SELECT * FROM active3('pkg:Model:BlobA');
Nested Loop Left Join  (cost=61335.64..106116.40 rows=1 width=899) (actual time=1037.717..7639.465 rows=477313 loops=1)
  ->  Nested Loop Left Join  (cost=61335.49..106116.23 rows=1 width=811) (actual time=1037.704..7411.266 rows=477313 loops=1)
        ->  Nested Loop Left Join  (cost=61335.06..106115.60 rows=1 width=778) (actual time=1037.700..7300.930 rows=477313 loops=1)
              ->  Nested Loop Left Join  (cost=61334.63..106114.97 rows=1 width=745) (actual time=1037.673..4827.817 rows=477313 loops=1)
                    ->  Nested Loop Left Join  (cost=61334.21..106114.50 rows=1 width=729) (actual time=1037.670..4719.031 rows=477313 loops=1)
                          ->  Nested Loop Left Join  (cost=61333.79..106114.03 rows=1 width=713) (actual time=1037.648..3502.760 rows=477313 loops=1)
                                ->  Nested Loop  (cost=61333.65..106105.86 rows=1 width=685) (actual time=1037.629..3265.222 rows=477313 loops=1)
                                      ->  Unique  (cost=61333.65..61428.86 rows=8646 width=150) (actual time=1037.566..1180.280 rows=477313 loops=1)
                                            InitPlan 1
                                              ->  Result  (cost=0.00..0.51 rows=1 width=8) (actual time=0.743..0.743 rows=1 loops=1)
                                            InitPlan 2
                                              ->  Result  (cost=0.00..0.01 rows=1 width=8) (actual time=0.012..0.012 rows=1 loops=1)
                                            ->  Sort  (cost=61333.13..61380.73 rows=19043 width=150) (actual time=1037.564..1132.812 rows=477313 loops=1)
                                                  Sort Key: __contracts.contract_id, __contracts.created_at_ix DESC
                                                  Sort Method: external merge  Disk: 75720kB
                                                  ->  Append  (cost=0.00..59979.46 rows=19043 width=150) (actual time=124.242..381.505 rows=477313 loops=1)
                                                        ->  Seq Scan on __contracts_1  (cost=0.00..0.00 rows=1 width=40) (never executed)
                                                              Filter: ((NOT divulged_only) AND (life_ix @> (InitPlan 1).col1) AND (tpe_pk = (InitPlan 2).col1))
                                                        ->  Seq Scan on __contracts_2  (cost=0.00..0.00 rows=1 width=40) (never executed)
                                                              Filter: ((NOT divulged_only) AND (life_ix @> (InitPlan 1).col1) AND (tpe_pk = (InitPlan 2).col1))
                                                        ->  Seq Scan on __contracts_3  (cost=0.00..0.00 rows=1 width=40) (never executed)
                                                              Filter: ((NOT divulged_only) AND (life_ix @> (InitPlan 1).col1) AND (tpe_pk = (InitPlan 2).col1))
                                                        ->  Seq Scan on __contracts_4  (cost=0.00..0.00 rows=1 width=40) (never executed)
                                                              Filter: ((NOT divulged_only) AND (life_ix @> (InitPlan 1).col1) AND (tpe_pk = (InitPlan 2).col1))
                                                        ->  Seq Scan on __contracts_5  (cost=0.00..0.00 rows=1 width=40) (never executed)
                                                              Filter: ((NOT divulged_only) AND (life_ix @> (InitPlan 1).col1) AND (tpe_pk = (InitPlan 2).col1))
                                                        ->  Seq Scan on __contracts_6  (cost=0.00..0.00 rows=1 width=40) (never executed)
                                                              Filter: ((NOT divulged_only) AND (life_ix @> (InitPlan 1).col1) AND (tpe_pk = (InitPlan 2).col1))
                                                        ->  Bitmap Heap Scan on __contracts_7  (cost=470.18..29493.58 rows=9519 width=150) (never executed)
                                                              Recheck Cond: ((life_ix @> (InitPlan 1).col1) AND (NOT divulged_only))
                                                              Filter: (tpe_pk = (InitPlan 2).col1)
                                                              ->  Bitmap Index Scan on __contracts_7_life_ix_tpe_pk_idx  (cost=0.00..467.81 rows=9519 width=0) (never executed)
                                                                    Index Cond: (life_ix @> (InitPlan 1).col1)
                                                        ->  Bitmap Heap Scan on __contracts_8  (cost=179.88..11381.31 rows=3561 width=150) (actual time=78.251..314.739 rows=477313 loops=1)
                                                              Recheck Cond: ((life_ix @> (InitPlan 1).col1) AND (NOT divulged_only))
                                                              Filter: (tpe_pk = (InitPlan 2).col1)
                                                              Heap Blocks: exact=52880
                                                              ->  Bitmap Index Scan on __contracts_8_life_ix_tpe_pk_idx  (cost=0.00..178.99 rows=3561 width=0) (actual time=73.197..73.197 rows=522271 loops=1)
                                                                    Index Cond: (life_ix @> (InitPlan 1).col1)
                                                        ->  Bitmap Heap Scan on __contracts_9  (cost=141.85..8880.80 rows=2782 width=150) (never executed)
                                                              Recheck Cond: ((life_ix @> (InitPlan 1).col1) AND (NOT divulged_only))
                                                              Filter: (tpe_pk = (InitPlan 2).col1)
                                                              ->  Bitmap Index Scan on __contracts_9_life_ix_tpe_pk_idx  (cost=0.00..141.15 rows=2782 width=0) (never executed)
                                                                    Index Cond: (life_ix @> (InitPlan 1).col1)
                                                        ->  Bitmap Heap Scan on __contracts_10  (cost=99.64..6316.94 rows=1982 width=150) (never executed)
                                                              Recheck Cond: ((life_ix @> (InitPlan 1).col1) AND (NOT divulged_only))
                                                              Filter: (tpe_pk = (InitPlan 2).col1)
                                                              ->  Bitmap Index Scan on __contracts_10_life_ix_tpe_pk_idx  (cost=0.00..99.15 rows=1982 width=0) (never executed)
                                                                    Index Cond: (life_ix @> (InitPlan 1).col1)
                                                        ->  Bitmap Heap Scan on __contracts_11  (cost=61.51..3810.58 rows=1191 width=150) (never executed)
                                                              Recheck Cond: ((life_ix @> (InitPlan 1).col1) AND (NOT divulged_only))
                                                              Filter: (tpe_pk = (InitPlan 2).col1)
                                                              ->  Bitmap Index Scan on __contracts_11_life_ix_tpe_pk_idx  (cost=0.00..61.21 rows=1191 width=0) (never executed)
                                                                    Index Cond: (life_ix @> (InitPlan 1).col1)
                                                        ->  Seq Scan on __contracts_12  (cost=0.00..0.00 rows=1 width=40) (never executed)
                                                              Filter: ((NOT divulged_only) AND (life_ix @> (InitPlan 1).col1) AND (tpe_pk = (InitPlan 2).col1))
                                                        ->  Seq Scan on __contracts_13  (cost=0.00..1.03 rows=1 width=150) (never executed)
                                                              Filter: ((NOT divulged_only) AND (life_ix @> (InitPlan 1).col1) AND (tpe_pk = (InitPlan 2).col1))
                                      ->  Index Scan using __contracts_8_contract_id_idx on __contracts_8 c  (cost=0.00..5.16 rows=1 width=685) (actual time=0.004..0.004 rows=1 loops=477313)
                                            Index Cond: (contract_id = __contracts.contract_id)
                                            Rows Removed by Index Recheck: 0
                                            Filter: ((tpe_pk = '8'::bigint) AND (__contracts.created_at_ix = created_at_ix))
                                ->  Index Scan using __contract_tpe_pkey on __contract_tpe tpe  (cost=0.15..8.17 rows=1 width=44) (actual time=0.000..0.000 rows=1 loops=477313)
                                      Index Cond: (pk = '8'::bigint)
                          ->  Index Scan using __transactions_pkey on __transactions ct  (cost=0.42..0.47 rows=1 width=24) (actual time=0.002..0.002 rows=1 loops=477313)
                                Index Cond: (ix = c.created_at_ix)
                    ->  Index Scan using __transactions_pkey on __transactions at  (cost=0.42..0.47 rows=1 width=24) (actual time=0.000..0.000 rows=0 loops=477313)
                          Index Cond: (ix = c.archived_at_ix)
              ->  Index Scan using __events_pkey on __events ce  (cost=0.43..0.63 rows=1 width=41) (actual time=0.005..0.005 rows=1 loops=477313)
                    Index Cond: (pk = c.create_event_pk)
        ->  Index Scan using __events_pkey on __events ae  (cost=0.43..0.63 rows=1 width=41) (actual time=0.000..0.000 rows=0 loops=477313)
              Index Cond: (pk = c.archive_event_pk)
  ->  Index Scan using __packages_pkey on __packages p  (cost=0.15..0.17 rows=1 width=104) (actual time=0.000..0.000 rows=1 loops=477313)
        Index Cond: (pk = c.package_pk)
Planning Time: 18.647 ms
JIT:
  Functions: 113
  Options: Inlining false, Optimization false, Expressions true, Deforming true
  Timing: Generation 4.862 ms (Deform 2.985 ms), Inlining 0.000 ms, Optimization 2.573 ms, Emission 43.447 ms, Total 50.882 ms
Execution Time: 7677.399 ms

EXPLAIN (ANALYZE, BUFFERS, VERBOSE, SETTINGS) SELECT * FROM active4('pkg:Model:BlobA');
Nested Loop Left Join  (cost=11634.79..11929.84 rows=8 width=899) (actual time=1579.023..5146.939 rows=477313 loops=1)
  Output: tpe.template_fqn, tpe.payload_type, c.create_event_pk, ce.event_id, c.created_at_ix, ct."offset", c.archive_event_pk, ae.event_id, c.archived_at_ix, at."offset", c.life_ix, c.contract_id, c.payload, c.contract_key, c.metadata, ct.effective_at, at.effective_at, c.redaction_id, p.name, p.version, p.id, c.signatories, c.observers, c.witnesses, c.divulged_only, COALESCE(c.creation_package_id, p.id), c.contract_key_hash
  Inner Unique: true
  Buffers: shared hit=3329008 read=547384, temp read=93357 written=93422
  ->  Nested Loop Left Join  (cost=11634.36..11862.26 rows=8 width=866) (actual time=1579.019..5029.883 rows=477313 loops=1)
        Output: c.create_event_pk, c.created_at_ix, c.archive_event_pk, c.archived_at_ix, c.life_ix, c.contract_id, c.payload, c.contract_key, c.metadata, c.redaction_id, c.signatories, c.observers, c.witnesses, c.divulged_only, c.creation_package_id, c.contract_key_hash, tpe.template_fqn, tpe.payload_type, ct."offset", ct.effective_at, at."offset", at.effective_at, ce.event_id, p.name, p.version, p.id
        Inner Unique: true
        Buffers: shared hit=3329008 read=547384, temp read=93357 written=93422
        ->  Nested Loop Left Join  (cost=11633.93..11794.68 rows=8 width=833) (actual time=1578.986..2847.740 rows=477313 loops=1)
              Output: c.create_event_pk, c.created_at_ix, c.archive_event_pk, c.archived_at_ix, c.life_ix, c.contract_id, c.payload, c.contract_key, c.metadata, c.redaction_id, c.signatories, c.observers, c.witnesses, c.divulged_only, c.creation_package_id, c.contract_key_hash, tpe.template_fqn, tpe.payload_type, ct."offset", ct.effective_at, at."offset", at.effective_at, p.name, p.version, p.id
              Inner Unique: true
              Buffers: shared hit=1858555 read=108585, temp read=93357 written=93422
              ->  Nested Loop Left Join  (cost=11633.51..11727.18 rows=8 width=817) (actual time=1578.984..2742.565 rows=477313 loops=1)
                    Output: c.create_event_pk, c.created_at_ix, c.archive_event_pk, c.archived_at_ix, c.life_ix, c.contract_id, c.payload, c.contract_key, c.metadata, c.redaction_id, c.signatories, c.observers, c.witnesses, c.divulged_only, c.creation_package_id, c.contract_key_hash, tpe.template_fqn, tpe.payload_type, ct."offset", ct.effective_at, p.name, p.version, p.id
                    Inner Unique: true
                    Buffers: shared hit=1858555 read=108585, temp read=93357 written=93422
                    ->  Nested Loop Left Join  (cost=11633.09..11659.68 rows=8 width=801) (actual time=1578.963..1823.745 rows=477313 loops=1)
                          Output: c.create_event_pk, c.created_at_ix, c.archive_event_pk, c.archived_at_ix, c.life_ix, c.contract_id, c.payload, c.contract_key, c.metadata, c.redaction_id, c.signatories, c.observers, c.witnesses, c.divulged_only, c.creation_package_id, c.contract_key_hash, tpe.template_fqn, tpe.payload_type, p.name, p.version, p.id
                          Inner Unique: true
                          Buffers: shared hit=3 read=57885, temp read=93357 written=93422
                          ->  Hash Right Join  (cost=11632.95..11651.41 rows=8 width=773) (actual time=1578.929..1745.808 rows=477313 loops=1)
                                Output: c.create_event_pk, c.created_at_ix, c.archive_event_pk, c.archived_at_ix, c.life_ix, c.contract_id, c.payload, c.contract_key, c.metadata, c.redaction_id, c.signatories, c.observers, c.witnesses, c.divulged_only, c.creation_package_id, c.contract_key_hash, c.tpe_pk, p.name, p.version, p.id
                                Hash Cond: (p.pk = c.package_pk)
                                Buffers: shared hit=3 read=57883, temp read=93357 written=93422
                                ->  Seq Scan on public.__packages p  (cost=0.00..16.10 rows=610 width=104) (actual time=0.045..0.046 rows=2 loops=1)
                                      Output: p.pk, p.name, p.version, p.id
                                      Buffers: shared read=1
                                ->  Hash  (cost=11632.85..11632.85 rows=8 width=685) (actual time=1489.895..1489.899 rows=477313 loops=1)
                                      Output: c.create_event_pk, c.created_at_ix, c.archive_event_pk, c.archived_at_ix, c.life_ix, c.contract_id, c.payload, c.contract_key, c.metadata, c.redaction_id, c.signatories, c.observers, c.witnesses, c.divulged_only, c.creation_package_id, c.contract_key_hash, c.tpe_pk, c.package_pk
                                      Buckets: 16384 (originally 1024)  Batches: 2 (originally 1)  Memory Usage: 257302kB
                                      Buffers: shared hit=3 read=57882, temp read=61892 written=93420
                                      ->  Subquery Scan on c  (cost=11594.82..11632.85 rows=8 width=685) (actual time=1023.416..1325.834 rows=477313 loops=1)
                                            Output: c.create_event_pk, c.created_at_ix, c.archive_event_pk, c.archived_at_ix, c.life_ix, c.contract_id, c.payload, c.contract_key, c.metadata, c.redaction_id, c.signatories, c.observers, c.witnesses, c.divulged_only, c.creation_package_id, c.contract_key_hash, c.tpe_pk, c.package_pk
                                            Filter: (c.tpe_pk = '8'::bigint)
                                            Buffers: shared hit=3 read=57882, temp read=61892 written=61957
                                            ->  Unique  (cost=11594.82..11612.63 rows=1617 width=685) (actual time=1023.412..1264.642 rows=477313 loops=1)
                                                  Output: c_1.tpe_pk, c_1.create_event_pk, c_1.created_at_ix, c_1.archive_event_pk, c_1.archived_at_ix, c_1.life_ix, c_1.contract_id, c_1.payload, c_1.contract_key, c_1.metadata, c_1.redaction_id, c_1.package_pk, c_1.signatories, c_1.observers, c_1.witnesses, c_1.divulged_only, c_1.creation_package_id, c_1.contract_key_hash
                                                  Buffers: shared hit=3 read=57882, temp read=61892 written=61957
                                                  InitPlan 1
                                                    ->  Result  (cost=0.00..0.51 rows=1 width=8) (actual time=0.896..0.897 rows=1 loops=1)
                                                          Output: __nearest_ix_floor(latest_offset())
                                                          Buffers: shared hit=3 read=8
                                                  ->  Sort  (cost=11594.31..11603.22 rows=3562 width=685) (actual time=1023.410..1212.919 rows=477313 loops=1)
                                                        Output: c_1.tpe_pk, c_1.create_event_pk, c_1.created_at_ix, c_1.archive_event_pk, c_1.archived_at_ix, c_1.life_ix, c_1.contract_id, c_1.payload, c_1.contract_key, c_1.metadata, c_1.redaction_id, c_1.package_pk, c_1.signatories, c_1.observers, c_1.witnesses, c_1.divulged_only, c_1.creation_package_id, c_1.contract_key_hash
                                                        Sort Key: c_1.contract_id, c_1.created_at_ix DESC
                                                        Sort Method: external merge  Disk: 247576kB
                                                        Buffers: shared hit=3 read=57882, temp read=61892 written=61957
                                                        ->  Bitmap Heap Scan on public.__contracts_8 c_1  (cost=179.89..11384.18 rows=3562 width=685) (actual time=81.015..286.365 rows=477313 loops=1)
                                                              Output: c_1.tpe_pk, c_1.create_event_pk, c_1.created_at_ix, c_1.archive_event_pk, c_1.archived_at_ix, c_1.life_ix, c_1.contract_id, c_1.payload, c_1.contract_key, c_1.metadata, c_1.redaction_id, c_1.package_pk, c_1.signatories, c_1.observers, c_1.witnesses, c_1.divulged_only, c_1.creation_package_id, c_1.contract_key_hash
                                                              Recheck Cond: ((c_1.life_ix @> (InitPlan 1).col1) AND (NOT c_1.divulged_only))
                                                              Filter: (c_1.tpe_pk = '8'::bigint)
                                                              Heap Blocks: exact=52880
                                                              Buffers: shared hit=3 read=57882
                                                              ->  Bitmap Index Scan on __contracts_8_life_ix_tpe_pk_idx  (cost=0.00..179.00 rows=3562 width=0) (actual time=75.404..75.404 rows=522271 loops=1)
                                                                    Index Cond: (c_1.life_ix @> (InitPlan 1).col1)
                                                                    Buffers: shared hit=3 read=5002
                          ->  Materialize  (cost=0.15..8.17 rows=1 width=44) (actual time=0.000..0.000 rows=1 loops=477313)
                                Output: tpe.template_fqn, tpe.payload_type, tpe.pk
                                Buffers: shared read=2
                                ->  Index Scan using __contract_tpe_pkey on public.__contract_tpe tpe  (cost=0.15..8.17 rows=1 width=44) (actual time=0.022..0.022 rows=1 loops=1)
                                      Output: tpe.template_fqn, tpe.payload_type, tpe.pk
                                      Index Cond: (tpe.pk = '8'::bigint)
                                      Buffers: shared read=2
                    ->  Index Scan using __transactions_pkey on public.__transactions ct  (cost=0.42..8.44 rows=1 width=24) (actual time=0.002..0.002 rows=1 loops=477313)
                          Output: ct."offset", ct.effective_at, ct.ix
                          Index Cond: (ct.ix = c.created_at_ix)
                          Buffers: shared hit=1858552 read=50700
              ->  Index Scan using __transactions_pkey on public.__transactions at  (cost=0.42..8.44 rows=1 width=24) (actual time=0.000..0.000 rows=0 loops=477313)
                    Output: at."offset", at.effective_at, at.ix
                    Index Cond: (at.ix = c.archived_at_ix)
        ->  Index Scan using __events_pkey on public.__events ce  (cost=0.43..8.45 rows=1 width=41) (actual time=0.004..0.004 rows=1 loops=477313)
              Output: ce.event_id, ce.pk
              Index Cond: (ce.pk = c.create_event_pk)
              Buffers: shared hit=1470453 read=438799
  ->  Index Scan using __events_pkey on public.__events ae  (cost=0.43..8.45 rows=1 width=41) (actual time=0.000..0.000 rows=0 loops=477313)
        Output: ae.event_id, ae.pk
        Index Cond: (ae.pk = c.archive_event_pk)
Planning:
  Buffers: shared hit=3 read=1
Planning Time: 3.700 ms
Execution Time: 5175.510 ms

SELECT summary_active();


ANALYZE __contracts;