create function prune_to_offset_selectively(min_offset checkpoint."offset"%type)
    returns table
            (
                pruning_boundary bigint,
                deleted_contracts integer,
                deleted_exercises integer,
                deleted_events integer,
                deleted_transactions integer
            )
as $$
declare
    cutoff_ix checkpoint.ix%type;
    new_oldest bigint;
begin
    -- Log the offset
    raise log 'Pruning selectively to offset: %', min_offset;

    -- Validate the provided offset
    select validation.squash_inclusive, validation.new_oldest
    into pruning_boundary, new_oldest
    from validate_pruning_offset(min_offset) as validation;

    select ix into cutoff_ix from __transactions where "offset" = new_oldest;
    
    with deleted_contracts as (
        delete from __contracts
        where
            -- prune contracts that were archived prior to cutoff
            archived_at_ix < cutoff_ix
            -- prune divulged-only contracts that existed prior to cutoff
            or (divulged_only and created_at_ix < cutoff_ix)
        returning create_event_pk, archive_event_pk
    ),
    -- prune exercises that happened prior to cutoff
    deleted_exercises as (
        delete from __exercises where exercised_at_ix < cutoff_ix
        returning exercise_event_pk
    ),
    -- prune create, archive and exercise events
    deleted_events as (
        delete from __events
        where tx_ix < cutoff_ix
        and (
            pk in (select create_event_pk from deleted_contracts)
            or pk in (select archive_event_pk from deleted_contracts)
            or pk in (select exercise_event_pk from deleted_exercises)
        )
        returning 1
    )
    select 
        (select count(*) from deleted_contracts),
        (select count(*) from deleted_exercises),
        (select count(*) from deleted_events)
    into deleted_contracts, deleted_exercises, deleted_events;

    -- prune orphaned transactions
    with deleted_transactions as (
        delete from __transactions
        where ix < cutoff_ix and not exists (
            select 1 from __contracts where __contracts.created_at_ix = __transactions.ix
        )
        returning 1
    )
    select count(*) into deleted_transactions from deleted_transactions;

    raise log 'Pruned % contracts, % exercises, % events and % transactions', 
        deleted_contracts, deleted_exercises, deleted_events, deleted_transactions;

    return query select pruning_boundary, deleted_contracts, deleted_exercises, deleted_events, deleted_transactions;
end;
$$ language plpgsql strict;
