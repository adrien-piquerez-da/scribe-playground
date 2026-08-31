package model;

import com.daml.ledger.javaapi.data.ContractFilter;
import com.daml.ledger.javaapi.data.CreateAndExerciseCommand;
import com.daml.ledger.javaapi.data.ExerciseByKeyCommand;
import com.daml.ledger.javaapi.data.ExerciseCommand;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.PackageVersion;
import com.daml.ledger.javaapi.data.Template;
import com.daml.ledger.javaapi.data.Unit;
import com.daml.ledger.javaapi.data.Value;
import com.daml.ledger.javaapi.data.codegen.Choice;
import com.daml.ledger.javaapi.data.codegen.Contract;
import com.daml.ledger.javaapi.data.codegen.ContractCompanion;
import com.daml.ledger.javaapi.data.codegen.ContractTypeCompanion;
import com.daml.ledger.javaapi.data.codegen.Exercised;
import com.daml.ledger.javaapi.data.codegen.InterfaceCompanion;
import com.daml.ledger.javaapi.data.codegen.PrimitiveValueDecoders;
import com.daml.ledger.javaapi.data.codegen.Update;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders;
import da.internal.template.Archive;
import java.lang.Override;
import java.lang.String;
import java.util.List;

public final class Blob {
  public static final Identifier TEMPLATE_ID = new Identifier("#pkg", "Model", "Blob");

  public static final Identifier TEMPLATE_ID_WITH_PACKAGE_ID = new Identifier("37555a1acf1d7776167b8803ea333d0c47c5c9cf07086db1b1124891d971e134", "Model", "Blob");

  public static final Identifier INTERFACE_ID = new Identifier("#pkg", "Model", "Blob");

  public static final Identifier INTERFACE_ID_WITH_PACKAGE_ID = new Identifier("37555a1acf1d7776167b8803ea333d0c47c5c9cf07086db1b1124891d971e134", "Model", "Blob");

  public static final String PACKAGE_ID = "37555a1acf1d7776167b8803ea333d0c47c5c9cf07086db1b1124891d971e134";

  public static final String PACKAGE_NAME = "pkg";

  public static final PackageVersion PACKAGE_VERSION = new PackageVersion(new int[] {1, 0, 0});

  public static final Choice<Blob, Archive, Unit> CHOICE_Archive = 
      Choice.create("Archive", value$ -> value$.toValue(),Archive.valueDecoder(),PrimitiveValueDecoders.fromUnit,
        new Archive.JsonDecoders().get(), JsonLfDecoders.unit, Archive::jsonEncoder,
        JsonLfEncoders::unit);

  public static final INTERFACE_ INTERFACE = new INTERFACE_();

  private Blob() {
  }

  public static ContractFilter<Contract<ContractId, BlobView>> contractFilter() {
    return ContractFilter.of(INTERFACE);
  }

  public static final class ContractId extends com.daml.ledger.javaapi.data.codegen.ContractId<Blob> implements Exercises<ExerciseCommand> {
    public ContractId(String contractId) {
      super(contractId);
    }

    @Override
    protected ContractTypeCompanion<? extends Contract<ContractId, ?>, ContractId, Blob, ?> getCompanion(
        ) {
      return INTERFACE;
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

  public static final class CreateAnd extends com.daml.ledger.javaapi.data.codegen.CreateAnd.ToInterface implements Exercises<CreateAndExerciseCommand> {
    public CreateAnd(ContractCompanion<?, ?, ?> companion, Template createArguments) {
      super(companion, createArguments);
    }

    @Override
    protected ContractTypeCompanion<? extends Contract<ContractId, ?>, ContractId, Blob, ?> getCompanion(
        ) {
      return INTERFACE;
    }
  }

  public static final class ByKey extends com.daml.ledger.javaapi.data.codegen.ByKey.ToInterface implements Exercises<ExerciseByKeyCommand> {
    public ByKey(ContractCompanion<?, ?, ?> companion, Value key) {
      super(companion, key);
    }

    @Override
    protected ContractTypeCompanion<? extends Contract<ContractId, ?>, ContractId, Blob, ?> getCompanion(
        ) {
      return INTERFACE;
    }
  }

  public static final class INTERFACE_ extends InterfaceCompanion<Blob, ContractId, BlobView> {
    INTERFACE_() {
      super(new ContractTypeCompanion.Package(Blob.PACKAGE_ID, Blob.PACKAGE_NAME, Blob.PACKAGE_VERSION),
            "model.Blob", Blob.TEMPLATE_ID, ContractId::new, BlobView.valueDecoder(),
            BlobView::fromJson,List.of(CHOICE_Archive));
    }
  }
}
