package seed;

import static com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders.apply;

import com.daml.ledger.javaapi.data.DamlCollectors;
import com.daml.ledger.javaapi.data.Value;
import com.daml.ledger.javaapi.data.codegen.DamlRecord;
import com.daml.ledger.javaapi.data.codegen.PrimitiveValueDecoders;
import com.daml.ledger.javaapi.data.codegen.ValueDecoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfReader;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ArchiveMany extends DamlRecord<ArchiveMany> {
  public static final String _packageId = "2e3386f68695579d46056ea8d2d28df24f3b1500f6fb1a484e4c1f884859b978";

  public final List<Blob.ContractId> contracts;

  public ArchiveMany(List<Blob.ContractId> contracts) {
    this.contracts = contracts;
  }

  public static ValueDecoder<ArchiveMany> valueDecoder() throws IllegalArgumentException {
    return value$ -> {
      Value recordValue$ = value$;
      List<com.daml.ledger.javaapi.data.DamlRecord.Field> fields$ = PrimitiveValueDecoders.recordCheck(1,0,
          recordValue$);
      List<Blob.ContractId> contracts = PrimitiveValueDecoders.fromList(v$0 ->
              new Blob.ContractId(v$0.asContractId().orElseThrow(() -> new IllegalArgumentException("Expected contracts to be of type com.daml.ledger.javaapi.data.ContractId")).getValue()))
          .decode(fields$.get(0).getValue());
      return new ArchiveMany(contracts);
    } ;
  }

  public com.daml.ledger.javaapi.data.DamlRecord toValue() {
    ArrayList<com.daml.ledger.javaapi.data.DamlRecord.Field> fields = new ArrayList<com.daml.ledger.javaapi.data.DamlRecord.Field>(1);
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("contracts", this.contracts.stream().collect(DamlCollectors.toDamlList(v$0 -> v$0.toValue()))));
    return new com.daml.ledger.javaapi.data.DamlRecord(fields);
  }

  public static JsonLfDecoder<ArchiveMany> jsonDecoder() {
    return JsonLfDecoders.record(Arrays.asList("contracts"), name -> {
          switch (name) {
            case "contracts": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(0, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.list(com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.contractId(seed.Blob.ContractId::new)));
            default: return null;
          }
        }
        , (Object[] args) -> new ArchiveMany(JsonLfDecoders.cast(args[0])));
  }

  public static ArchiveMany fromJson(String json) throws JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json));
  }

  public JsonLfEncoder jsonEncoder() {
    return JsonLfEncoders.record(
        JsonLfEncoders.Field.of("contracts", apply(JsonLfEncoders.list(JsonLfEncoders::contractId), contracts)));
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (!(object instanceof ArchiveMany)) {
      return false;
    }
    ArchiveMany other = (ArchiveMany) object;
    return Objects.equals(this.contracts, other.contracts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.contracts);
  }

  @Override
  public String toString() {
    return String.format("seed.ArchiveMany(%s)", this.contracts);
  }

  /**
   * Proxies the jsonDecoder(...) static method, to provide an alternative calling synatx, which avoids some cases in generated code where javac gets confused
   */
  public static class JsonDecoders {
    public JsonLfDecoder<ArchiveMany> get() {
      return jsonDecoder();
    }
  }
}
