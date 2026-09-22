package model;

import static com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders.apply;

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
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BlobView extends DamlRecord<BlobView> {
  public static final String _packageId = "9922a1da9495bff57659bc60acf007825c2899e8f708c315129a560f9669e616";

  public BlobView() {
  }

  public static ValueDecoder<BlobView> valueDecoder() throws IllegalArgumentException {
    return ValueDecoder.create((value$, policy$) -> {
      Value recordValue$ = value$;
      PreparedRecord preparedRecord$ = PrimitiveValueDecoders.checkAndPrepareRecord(0,0,
          recordValue$,  policy$);
      java.util.List<com.daml.ledger.javaapi.data.DamlRecord.Field> fields$ = preparedRecord$.getExpectedFields();
      return new BlobView();
    });
  }

  public com.daml.ledger.javaapi.data.DamlRecord toValue() {
    ArrayList<com.daml.ledger.javaapi.data.DamlRecord.Field> fields = new ArrayList<com.daml.ledger.javaapi.data.DamlRecord.Field>(0);
    return new com.daml.ledger.javaapi.data.DamlRecord(fields);
  }

  public static JsonLfDecoder<BlobView> jsonDecoder() {
    return JsonLfDecoders.record(Arrays.asList(), name -> {
          switch (name) {
            default: return null;
          }
        }
        , (Object[] args) -> new BlobView());
  }

  public static BlobView fromJson(String json) throws JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json), UnknownTrailingFieldPolicy.STRICT);
  }

  public static BlobView fromJson(String json, UnknownTrailingFieldPolicy policy) throws
      JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json), policy);
  }

  public JsonLfEncoder jsonEncoder() {
    return JsonLfEncoders.record();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (!(object instanceof BlobView)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }

  @Override
  public String toString() {
    return "model.BlobView";
  }

  /**
   * Proxies the jsonDecoder(...) static method, to provide an alternative calling synatx, which avoids some cases in generated code where javac gets confused
   */
  public static class JsonDecoders {
    public JsonLfDecoder<BlobView> get() {
      return jsonDecoder();
    }
  }
}
