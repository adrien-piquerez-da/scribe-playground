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
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Seed extends Template {
  public static final Identifier TEMPLATE_ID = new Identifier("#pkg", "Model", "Seed");

  public static final Identifier TEMPLATE_ID_WITH_PACKAGE_ID = new Identifier("37555a1acf1d7776167b8803ea333d0c47c5c9cf07086db1b1124891d971e134", "Model", "Seed");

  public static final String PACKAGE_ID = "37555a1acf1d7776167b8803ea333d0c47c5c9cf07086db1b1124891d971e134";

  public static final String PACKAGE_NAME = "pkg";

  public static final PackageVersion PACKAGE_VERSION = new PackageVersion(new int[] {1, 0, 0});

  public static final Choice<Seed, Archive, Unit> CHOICE_Archive = 
      Choice.create("Archive", value$ -> value$.toValue(),Archive.valueDecoder(),PrimitiveValueDecoders.fromUnit,
        new Archive.JsonDecoders().get(), JsonLfDecoders.unit, Archive::jsonEncoder,
        JsonLfEncoders::unit);

  public static final Choice<Seed, CreateMany, List<Blob.ContractId>> CHOICE_CreateMany = 
      Choice.create("CreateMany", value$ -> value$.toValue(),CreateMany.valueDecoder(),PrimitiveValueDecoders.fromList(
          v$0 ->
            new Blob.ContractId(v$0.asContractId().orElseThrow(() -> new IllegalArgumentException("Expected value$ to be of type com.daml.ledger.javaapi.data.ContractId")).getValue())),
        new CreateMany.JsonDecoders().get(),
        JsonLfDecoders.list(JsonLfDecoders.contractId(Blob.ContractId::new)),
        CreateMany::jsonEncoder, JsonLfEncoders.list(JsonLfEncoders::contractId));

  public static final Choice<Seed, ArchiveMany, Unit> CHOICE_ArchiveMany = 
      Choice.create("ArchiveMany", value$ -> value$.toValue(),ArchiveMany.valueDecoder(),PrimitiveValueDecoders.fromUnit,
        new ArchiveMany.JsonDecoders().get(), JsonLfDecoders.unit, ArchiveMany::jsonEncoder,
        JsonLfEncoders::unit);

  public static final ContractCompanion.WithoutKey<Contract, ContractId, Seed> COMPANION = 
      new ContractCompanion.WithoutKey<>(new ContractTypeCompanion.Package(Seed.PACKAGE_ID, Seed.PACKAGE_NAME, Seed.PACKAGE_VERSION),
        "model.Seed", TEMPLATE_ID, ContractId::new, Seed::fromJson, Contract::new,
        List.of(CHOICE_Archive, CHOICE_CreateMany, CHOICE_ArchiveMany),Seed.templateValueDecoder());

  public final String owner;

  public Seed(String owner) {
    this.owner = owner;
  }

  @Override
  public Update<Created<ContractId>> create() {
    return new Update.CreateUpdate<ContractId, Created<ContractId>>(new CreateCommand(Seed.TEMPLATE_ID, this.toValue()), x -> x, ContractId::new);
  }

  public static Update<Created<ContractId>> create(String owner) {
    return new Seed(owner).create();
  }

  @Override
  public CreateAnd createAnd() {
    return new CreateAnd(this);
  }

  @Override
  protected ContractCompanion.WithoutKey<Contract, ContractId, Seed> getCompanion() {
    return COMPANION;
  }

  public static ValueDecoder<Seed> valueDecoder() throws IllegalArgumentException {
    final ValueDecoder<Seed> base = templateValueDecoder();
    return new ValueDecoder<Seed>() {
      @Override
      public Seed decode(Value value, UnknownTrailingFieldPolicy policy) {
        return base.decode(value, policy);
      }
      @Override
      public Seed decode(Value value) {
        return base.decode(value);
      }
      @Override
      public com.daml.ledger.javaapi.data.codegen.ContractId<Seed> fromContractId(String contractId) {
        return new ContractId(contractId);
      }
    };
  }

  public DamlRecord toValue() {
    ArrayList<DamlRecord.Field> fields = new ArrayList<DamlRecord.Field>(1);
    fields.add(new DamlRecord.Field("owner", new Party(this.owner)));
    return new DamlRecord(fields);
  }

  private static ValueDecoder<Seed> templateValueDecoder() throws IllegalArgumentException {
    return ValueDecoder.create((value$, policy$) -> {
      Value recordValue$ = value$;
      PreparedRecord preparedRecord$ = PrimitiveValueDecoders.checkAndPrepareRecord(1,0,
          recordValue$,  policy$);
      java.util.List<DamlRecord.Field> fields$ = preparedRecord$.getExpectedFields();
      String owner = PrimitiveValueDecoders.fromParty.decode(fields$.get(0).getValue(),policy$);
      return new Seed(owner);
    });
  }

  public static JsonLfDecoder<Seed> jsonDecoder() {
    return JsonLfDecoders.record(Arrays.asList("owner"), name -> {
          switch (name) {
            case "owner": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(0, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.party);
            default: return null;
          }
        }
        , (Object[] args) -> new Seed(JsonLfDecoders.cast(args[0])));
  }

  public static Seed fromJson(String json) throws JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json), UnknownTrailingFieldPolicy.STRICT);
  }

  public static Seed fromJson(String json, UnknownTrailingFieldPolicy policy) throws
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
    if (!(object instanceof Seed)) {
      return false;
    }
    Seed other = (Seed) object;
    return Objects.equals(this.owner, other.owner);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.owner);
  }

  @Override
  public String toString() {
    return String.format("model.Seed(%s)", this.owner);
  }

  public static final class ContractId extends com.daml.ledger.javaapi.data.codegen.ContractId<Seed> implements Exercises<ExerciseCommand> {
    public ContractId(String contractId) {
      super(contractId);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, Seed, ?> getCompanion(
        ) {
      return COMPANION;
    }

    public static ContractId fromContractId(
        com.daml.ledger.javaapi.data.codegen.ContractId<Seed> contractId) {
      return COMPANION.toContractId(contractId);
    }
  }

  public static class Contract extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, Seed> {
    public Contract(ContractId id, Seed data, Set<String> signatories, Set<String> observers) {
      super(id, data, signatories, observers);
    }

    @Override
    protected ContractCompanion<Contract, ContractId, Seed> getCompanion() {
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

    default Update<Exercised<List<Blob.ContractId>>> exerciseCreateMany(CreateMany arg) {
      return makeExerciseCmd(CHOICE_CreateMany, arg);
    }

    default Update<Exercised<List<Blob.ContractId>>> exerciseCreateMany(Long a, Long b, Long c,
        Long d) {
      return exerciseCreateMany(new CreateMany(a, b, c, d));
    }

    default Update<Exercised<Unit>> exerciseArchiveMany(ArchiveMany arg) {
      return makeExerciseCmd(CHOICE_ArchiveMany, arg);
    }

    default Update<Exercised<Unit>> exerciseArchiveMany(List<Blob.ContractId> contracts) {
      return exerciseArchiveMany(new ArchiveMany(contracts));
    }
  }

  public static final class CreateAnd extends com.daml.ledger.javaapi.data.codegen.CreateAnd implements Exercises<CreateAndExerciseCommand> {
    CreateAnd(Template createArguments) {
      super(createArguments);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, Seed, ?> getCompanion(
        ) {
      return COMPANION;
    }
  }

  /**
   * Proxies the jsonDecoder(...) static method, to provide an alternative calling synatx, which avoids some cases in generated code where javac gets confused
   */
  public static class JsonDecoders {
    public JsonLfDecoder<Seed> get() {
      return jsonDecoder();
    }
  }
}
