package model;

import static com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders.apply;

import com.daml.ledger.javaapi.data.Int64;
import com.daml.ledger.javaapi.data.Value;
import com.daml.ledger.javaapi.data.codegen.DamlRecord;
import com.daml.ledger.javaapi.data.codegen.PreparedRecord;
import com.daml.ledger.javaapi.data.codegen.PrimitiveValueDecoders;
import com.daml.ledger.javaapi.data.codegen.UnknownTrailingFieldPolicy;
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
import java.util.Objects;

public class CreateMany extends DamlRecord<CreateMany> {
  public static final String _packageId = "9922a1da9495bff57659bc60acf007825c2899e8f708c315129a560f9669e616";

  public final Long a;

  public final Long b;

  public final Long c;

  public final Long d;

  public final Long e;

  public final Long f;

  public final Long g;

  public final Long h;

  public CreateMany(Long a, Long b, Long c, Long d, Long e, Long f, Long g, Long h) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
    this.f = f;
    this.g = g;
    this.h = h;
  }

  public static ValueDecoder<CreateMany> valueDecoder() throws IllegalArgumentException {
    return ValueDecoder.create((value$, policy$) -> {
      Value recordValue$ = value$;
      PreparedRecord preparedRecord$ = PrimitiveValueDecoders.checkAndPrepareRecord(8,0,
          recordValue$,  policy$);
      java.util.List<com.daml.ledger.javaapi.data.DamlRecord.Field> fields$ = preparedRecord$.getExpectedFields();
      Long a = PrimitiveValueDecoders.fromInt64.decode(fields$.get(0).getValue(),policy$);
      Long b = PrimitiveValueDecoders.fromInt64.decode(fields$.get(1).getValue(),policy$);
      Long c = PrimitiveValueDecoders.fromInt64.decode(fields$.get(2).getValue(),policy$);
      Long d = PrimitiveValueDecoders.fromInt64.decode(fields$.get(3).getValue(),policy$);
      Long e = PrimitiveValueDecoders.fromInt64.decode(fields$.get(4).getValue(),policy$);
      Long f = PrimitiveValueDecoders.fromInt64.decode(fields$.get(5).getValue(),policy$);
      Long g = PrimitiveValueDecoders.fromInt64.decode(fields$.get(6).getValue(),policy$);
      Long h = PrimitiveValueDecoders.fromInt64.decode(fields$.get(7).getValue(),policy$);
      return new CreateMany(a, b, c, d, e, f, g, h);
    });
  }

  public com.daml.ledger.javaapi.data.DamlRecord toValue() {
    ArrayList<com.daml.ledger.javaapi.data.DamlRecord.Field> fields = new ArrayList<com.daml.ledger.javaapi.data.DamlRecord.Field>(8);
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("a", new Int64(this.a)));
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("b", new Int64(this.b)));
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("c", new Int64(this.c)));
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("d", new Int64(this.d)));
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("e", new Int64(this.e)));
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("f", new Int64(this.f)));
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("g", new Int64(this.g)));
    fields.add(new com.daml.ledger.javaapi.data.DamlRecord.Field("h", new Int64(this.h)));
    return new com.daml.ledger.javaapi.data.DamlRecord(fields);
  }

  public static JsonLfDecoder<CreateMany> jsonDecoder() {
    return JsonLfDecoders.record(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"), name -> {
          switch (name) {
            case "a": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(0, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            case "b": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(1, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            case "c": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(2, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            case "d": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(3, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            case "e": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(4, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            case "f": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(5, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            case "g": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(6, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            case "h": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(7, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.int64);
            default: return null;
          }
        }
        , (Object[] args) -> new CreateMany(JsonLfDecoders.cast(args[0]), JsonLfDecoders.cast(args[1]), JsonLfDecoders.cast(args[2]), JsonLfDecoders.cast(args[3]), JsonLfDecoders.cast(args[4]), JsonLfDecoders.cast(args[5]), JsonLfDecoders.cast(args[6]), JsonLfDecoders.cast(args[7])));
  }

  public static CreateMany fromJson(String json) throws JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json), UnknownTrailingFieldPolicy.STRICT);
  }

  public static CreateMany fromJson(String json, UnknownTrailingFieldPolicy policy) throws
      JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json), policy);
  }

  public JsonLfEncoder jsonEncoder() {
    return JsonLfEncoders.record(JsonLfEncoders.Field.of("a", apply(JsonLfEncoders::int64, a)),
        JsonLfEncoders.Field.of("b", apply(JsonLfEncoders::int64, b)),
        JsonLfEncoders.Field.of("c", apply(JsonLfEncoders::int64, c)),
        JsonLfEncoders.Field.of("d", apply(JsonLfEncoders::int64, d)),
        JsonLfEncoders.Field.of("e", apply(JsonLfEncoders::int64, e)),
        JsonLfEncoders.Field.of("f", apply(JsonLfEncoders::int64, f)),
        JsonLfEncoders.Field.of("g", apply(JsonLfEncoders::int64, g)),
        JsonLfEncoders.Field.of("h", apply(JsonLfEncoders::int64, h)));
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
        Objects.equals(this.c, other.c) && Objects.equals(this.d, other.d) &&
        Objects.equals(this.e, other.e) && Objects.equals(this.f, other.f) &&
        Objects.equals(this.g, other.g) && Objects.equals(this.h, other.h);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h);
  }

  @Override
  public String toString() {
    return String.format("model.CreateMany(%s, %s, %s, %s, %s, %s, %s, %s)", this.a, this.b, this.c,
        this.d, this.e, this.f, this.g, this.h);
  }

  /**
   * Proxies the jsonDecoder(...) static method, to provide an alternative calling synatx, which avoids some cases in generated code where javac gets confused
   */
  public static class JsonDecoders {
    public JsonLfDecoder<CreateMany> get() {
      return jsonDecoder();
    }
  }
}
