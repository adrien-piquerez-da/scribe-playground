-- Active: 1770020971058@@127.0.0.1@5432@pqs


SELECT latest_offset()

SELECT * FROM summary_active2(120000);

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

create or replace function summary_active2(
    "offset" __transactions."offset"%type default latest_offset()
) returns setof contract_summary
as
$$
with stats as (
      select c.tpe_pk as tpe_pk, count(DISTINCT c.contract_id) as count
      from __contracts c
      where c.life_ix @> __nearest_ix_floor("offset")
            and not c.divulged_only -- exclude contracts that were merely divulged
      group by c.tpe_pk
)
select tpe.template_fqn, tpe.payload_type, stats.count
from stats
join __contract_tpe tpe on stats.tpe_pk = tpe.pk
$$ language sql stable parallel safe;


create or replace function lookup_contract2(
    contract_id __contracts.contract_id%type,
    qname text default null
) returns setof contract as
$$
  SELECT DISTINCT ON (c.template_fqn, c.contract_id) c.*
  FROM __contracts(qname) c
  WHERE c.contract_id = lookup_contract2.contract_id
  ORDER BY c.template_fqn, c.contract_id, c.created_at_offset DESC
$$ language sql stable;

BEGIN;

SET work_mem = '512MB';

EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM active2('pkg:Model:Blob');

COMMIT;


