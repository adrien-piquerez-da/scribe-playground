package model;

import static com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders.apply;

import com.daml.ledger.javaapi.data.ContractFilter;
import com.daml.ledger.javaapi.data.CreateAndExerciseCommand;
import com.daml.ledger.javaapi.data.CreateCommand;
import com.daml.ledger.javaapi.data.CreatedEvent;
import com.daml.ledger.javaapi.data.DamlRecord;
import com.daml.ledger.javaapi.data.ExerciseCommand;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.PackageVersion;
import com.daml.ledger.javaapi.data.Party;
import com.daml.ledger.javaapi.data.Template;
import com.daml.ledger.javaapi.data.Unit;
import com.daml.ledger.javaapi.data.Value;
import com.daml.ledger.javaapi.data.codegen.Choice;
import com.daml.ledger.javaapi.data.codegen.ContractCompanion;
import com.daml.ledger.javaapi.data.codegen.ContractTypeCompanion;
import com.daml.ledger.javaapi.data.codegen.Created;
import com.daml.ledger.javaapi.data.codegen.Exercised;
import com.daml.ledger.javaapi.data.codegen.PreparedRecord;
import com.daml.ledger.javaapi.data.codegen.PrimitiveValueDecoders;
import com.daml.ledger.javaapi.data.codegen.UnknownTrailingFieldPolicy;
import com.daml.ledger.javaapi.data.codegen.Update;
import com.daml.ledger.javaapi.data.codegen.ValueDecoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfReader;
import da.internal.template.Archive;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class BlobG extends Template {
  public static final Identifier TEMPLATE_ID = new Identifier("#pkg2", "Model", "BlobG");

  public static final Identifier TEMPLATE_ID_WITH_PACKAGE_ID = new Identifier("9922a1da9495bff57659bc60acf007825c2899e8f708c315129a560f9669e616", "Model", "BlobG");

  public static final String PACKAGE_ID = "9922a1da9495bff57659bc60acf007825c2899e8f708c315129a560f9669e616";

  public static final String PACKAGE_NAME = "pkg2";

  public static final PackageVersion PACKAGE_VERSION = new PackageVersion(new int[] {1, 0, 0});

  public static final Choice<BlobG, Archive, Unit> CHOICE_Archive = 
      Choice.create("Archive", value$ -> value$.toValue(),Archive.valueDecoder(),PrimitiveValueDecoders.fromUnit,
        new Archive.JsonDecoders().get(), JsonLfDecoders.unit, Archive::jsonEncoder,
        JsonLfEncoders::unit);

  public static final ContractCompanion.WithoutKey<Contract, ContractId, BlobG> COMPANION = 
      new ContractCompanion.WithoutKey<>(new ContractTypeCompanion.Package(BlobG.PACKAGE_ID, BlobG.PACKAGE_NAME, BlobG.PACKAGE_VERSION),
        "model.BlobG", TEMPLATE_ID, ContractId::new, BlobG::fromJson, Contract::new,
        List.of(CHOICE_Archive),BlobG.templateValueDecoder());

  public final String owner;

  public BlobG(String owner) {
    this.owner = owner;
  }

  @Override
  public Update<Created<ContractId>> create() {
    return new Update.CreateUpdate<ContractId, Created<ContractId>>(new CreateCommand(BlobG.TEMPLATE_ID, this.toValue()), x -> x, ContractId::new);
  }

  public static Update<Created<ContractId>> create(String owner) {
    return new BlobG(owner).create();
  }

  @Override
  public CreateAnd createAnd() {
    return new CreateAnd(this);
  }

  @Override
  protected ContractCompanion.WithoutKey<Contract, ContractId, BlobG> getCompanion() {
    return COMPANION;
  }

  public static ValueDecoder<BlobG> valueDecoder() throws IllegalArgumentException {
    final ValueDecoder<BlobG> base = templateValueDecoder();
    return new ValueDecoder<BlobG>() {
      @Override
      public BlobG decode(Value value, UnknownTrailingFieldPolicy policy) {
        return base.decode(value, policy);
      }
      @Override
      public BlobG decode(Value value) {
        return base.decode(value);
      }
      @Override
      public com.daml.ledger.javaapi.data.codegen.ContractId<BlobG> fromContractId(String contractId) {
        return new ContractId(contractId);
      }
    };
  }

  public DamlRecord toValue() {
    ArrayList<DamlRecord.Field> fields = new ArrayList<DamlRecord.Field>(1);
    fields.add(new DamlRecord.Field("owner", new Party(this.owner)));
    return new DamlRecord(fields);
  }

  private static ValueDecoder<BlobG> templateValueDecoder() throws IllegalArgumentException {
    return ValueDecoder.create((value$, policy$) -> {
      Value recordValue$ = value$;
      PreparedRecord preparedRecord$ = PrimitiveValueDecoders.checkAndPrepareRecord(1,0,
          recordValue$,  policy$);
      java.util.List<DamlRecord.Field> fields$ = preparedRecord$.getExpectedFields();
      String owner = PrimitiveValueDecoders.fromParty.decode(fields$.get(0).getValue(),policy$);
      return new BlobG(owner);
    });
  }

  public static JsonLfDecoder<BlobG> jsonDecoder() {
    return JsonLfDecoders.record(Arrays.asList("owner"), name -> {
          switch (name) {
            case "owner": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(0, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.party);
            default: return null;
          }
        }
        , (Object[] args) -> new BlobG(JsonLfDecoders.cast(args[0])));
  }

  public static BlobG fromJson(String json) throws JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json), UnknownTrailingFieldPolicy.STRICT);
  }

  public static BlobG fromJson(String json, UnknownTrailingFieldPolicy policy) throws
      JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json), policy);
  }

  public JsonLfEncoder jsonEncoder() {
    return JsonLfEncoders.record(
        JsonLfEncoders.Field.of("owner", apply(JsonLfEncoders::party, owner)));
  }

  public static ContractFilter<Contract> contractFilter() {
    return ContractFilter.of(COMPANION);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (!(object instanceof BlobG)) {
      return false;
    }
    BlobG other = (BlobG) object;
    return Objects.equals(this.owner, other.owner);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.owner);
  }

  @Override
  public String toString() {
    return String.format("model.BlobG(%s)", this.owner);
  }

  public static final class ContractId extends com.daml.ledger.javaapi.data.codegen.ContractId<BlobG> implements Exercises<ExerciseCommand> {
    public ContractId(String contractId) {
      super(contractId);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, BlobG, ?> getCompanion(
        ) {
      return COMPANION;
    }

    public Blob.ContractId toInterface(Blob.INTERFACE_ interfaceCompanion) {
      return new Blob.ContractId(this.contractId);
    }

    public static ContractId unsafeFromInterface(Blob.ContractId interfaceContractId) {
      return new ContractId(interfaceContractId.contractId);
    }

    public static ContractId fromContractId(
        com.daml.ledger.javaapi.data.codegen.ContractId<BlobG> contractId) {
      return COMPANION.toContractId(contractId);
    }
  }

  public static class Contract extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, BlobG> {
    public Contract(ContractId id, BlobG data, Set<String> signatories, Set<String> observers) {
      super(id, data, signatories, observers);
    }

    @Override
    protected ContractCompanion<Contract, ContractId, BlobG> getCompanion() {
      return COMPANION;
    }

    public static Contract fromIdAndRecord(String contractId, DamlRecord record$,
        Set<String> signatories, Set<String> observers) {
      return COMPANION.fromIdAndRecord(contractId, record$, signatories, observers);
    }

    public static Contract fromCreatedEvent(CreatedEvent event) {
      return COMPANION.fromCreatedEvent(event);
    }

    public static Contract fromCreatedEvent(CreatedEvent event, UnknownTrailingFieldPolicy policy) {
      return COMPANION.fromCreatedEvent(event, policy);
    }
  }

  public interface Exercises<Cmd> extends com.daml.ledger.javaapi.data.codegen.Exercises.Archivable<Cmd> {
    default Update<Exercised<Unit>> exerciseArchive(Archive arg) {
      return makeExerciseCmd(CHOICE_Archive, arg);
    }

    default Update<Exercised<Unit>> exerciseArchive() {
      return exerciseArchive(new Archive());
    }
  }

  public static final class CreateAnd extends com.daml.ledger.javaapi.data.codegen.CreateAnd implements Exercises<CreateAndExerciseCommand> {
    CreateAnd(Template createArguments) {
      super(createArguments);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, BlobG, ?> getCompanion(
        ) {
      return COMPANION;
    }

    public Blob.CreateAnd toInterface(Blob.INTERFACE_ interfaceCompanion) {
      return new Blob.CreateAnd(COMPANION, this.createArguments);
    }
  }

  /**
   * Proxies the jsonDecoder(...) static method, to provide an alternative calling synatx, which avoids some cases in generated code where javac gets confused
   */
  public static class JsonDecoders {
    public JsonLfDecoder<BlobG> get() {
      return jsonDecoder();
    }
  }
}
