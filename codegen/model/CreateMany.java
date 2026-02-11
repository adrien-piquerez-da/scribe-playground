package model;

import static com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders.apply;

import com.daml.ledger.javaapi.data.Int64;
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
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CreateMany extends DamlRecord<CreateMany> {
  public static final String _packageId = "467ab7672e755fd2c39e677860c486de75e26c1aa4b1949b1740e72891a2458b";

  public final Long a;

  public final Long b;

  public final Long c;

  public final Long d;

  public CreateMany(Long a, Long b, Long c, Long d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  public static ValueDecoder<CreateMany> valueDecoder() throws IllegalArgumentException {
    return value$ -> {
      Value recordValue$ = value$;
      List<com.daml.ledger.javaapi.data.DamlRecord.Field> fields$ = PrimitiveValueDecoders.recordCheck(4,0,
          recordValue$);
      Long a = PrimitiveValueDecoders.fromInt64.decode(fields$.get(0).getValue());
      Long b = PrimitiveValueDecoders.fromInt64.decode(fields$.get(1).getValue());
      Long c = PrimitiveValueDecoders.fromInt64.decode(fields$.get(2).getValue());
      Long d = PrimitiveValueDecoders.fromInt64.decode(fields$.get(3).getValue());
      return new CreateMany(a, b, c, d);
    } ;
  }

  public com.daml.ledger.javaapi.data.DamlRecord toValue() {
    ArrayList<com.daml.ledger.javaapi.data.DamlRecord.Field> fields = new ArrayList<com.daml.ledger.javaapi.data.DamlRecord.Field>(4);
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("a", new Int64(this.a)));
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("b", new Int64(this.b)));
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("c", new Int64(this.c)));
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("d", new Int64(this.d)));
    return new com.daml.ledger.javaapi.data.DamlRecord(fields);
  }

  public static JsonLfDecoder<CreateMany> jsonDecoder() {
    return JsonLfDecoders.record(Arrays.asList("a", "b", "c", "d"), name -> {
          switch (name) {
            case "a": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(0, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            case "b": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(1, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            case "c": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(2, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            case "d": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(3, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            default: return null;
          }
        }
        , (Object[] args) -> new CreateMany(JsonLfDecoders.cast(args[0]), JsonLfDecoders.cast(args[1]), JsonLfDecoders.cast(args[2]), JsonLfDecoders.cast(args[3])));
  }

  public static CreateMany fromJson(String json) throws JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json));
  }

  public JsonLfEncoder jsonEncoder() {
    return JsonLfEncoders.record(JsonLfEncoders.Field.of("a", apply(JsonLfEncoders::int64, a)),
        JsonLfEncoders.Field.of("b", apply(JsonLfEncoders::int64, b)),
        JsonLfEncoders.Field.of("c", apply(JsonLfEncoders::int64, c)),
        JsonLfEncoders.Field.of("d", apply(JsonLfEncoders::int64, d)));
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (!(object instanceof CreateMany)) {
      return false;
    }
    CreateMany other = (CreateMany) object;
    return Objects.equals(this.a, other.a) && Objects.equals(this.b, other.b) &&
        Objects.equals(this.c, other.c) && Objects.equals(this.d, other.d);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.a, this.b, this.c, this.d);
  }

  @Override
  public String toString() {
    return String.format("model.CreateMany(%s, %s, %s, %s)", this.a, this.b, this.c, this.d);
  }

  /**
   * Proxies the jsonDecoder(...) static method, to provide an alternative calling synatx, which avoids some cases in generated code where javac gets confused
   */
  public static class JsonDecoder {
    public JsonLfDecoder<CreateMany> get() {
      return jsonDecoder();
    }
  }
}
