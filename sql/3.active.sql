-- Active: 1770020971058@@127.0.0.1@5432@pqs


SELECT latest_offset()

SELECT * FROM active('pkg2:Model:BlobH') WHERE signatories @> array['7aaa39b9-53a8-475d-bffe-c485a93ef700::1220bdc97081a26d4c3e4ee9c15ff87a57058da8c6e85792c11b66a74e9c09e0c54b'];

SELECT * FROM summary_active();

SELECT count(*) FROM active2('pkg2:Model:BlobH');


select * from active('pkg2:Model:BlobH') WHERE contract_id IN (
  '006223c49aa3d3479b570f085f58fab0a8781e859faf94d47a29972b6f353183b7ca121220496c04ae86deb6367c78781d13aa1af7990ce030d3c76aea4f776a071277e83b',
);