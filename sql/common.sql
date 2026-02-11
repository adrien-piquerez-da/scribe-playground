-- Active: 1770020971058@@127.0.0.1@5432@pqs
SELECT contract_id, payload->'name', created_at_offset, archived_at_offset FROM creates() WHERE template_fqn='pkg:Model:NamedBlob';

SELECT created_at_offset FROM active() WHERE contract_id='00a970d584801c858df580b3c8847d6bf064bc72edeba9161e54273936e66e22a9ca121220c31270caec36c68986aad0e9a86c4a2f5c95cb9be46f252f8b0b0b670cd37919';
