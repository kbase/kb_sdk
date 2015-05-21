package us.kbase.scripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import us.kbase.kidl.KbBasicType;
import us.kbase.kidl.KbList;
import us.kbase.kidl.KbMapping;
import us.kbase.kidl.KbScalar;
import us.kbase.kidl.KbTuple;
import us.kbase.kidl.KbUnspecifiedObject;

public class GwtGenerator {
	public static void generate(JavaData data, FileSaver srcOutDir, String gwtPackage) throws Exception {
		generatePojos(data, srcOutDir, gwtPackage);
		generateTupleClasses(data,srcOutDir, gwtPackage);
	}
	
	private static void generatePojos(JavaData data, FileSaver srcOutDir, String outPackage) throws Exception {
		String outDir = outPackage.replace('.', '/');
		for (JavaType type : data.getTypes()) {
			String className = type.getJavaClassName() + "GWT";
			String tupleFile = outDir + "/" + className + ".java";
			JavaImportHolder model = new JavaImportHolder(outPackage);
			List<String> varLines = new ArrayList<String>();
			List<String> methodLines = new ArrayList<String>();
			for (int fieldPos = 0; fieldPos < type.getInternalFields().size(); fieldPos++) {
				String fieldName = type.getInternalFields().get(fieldPos);
				String methodSuffix = TextUtils.capitalize(fieldName);
				JavaType fieldType = type.getInternalTypes().get(fieldPos);
				String fieldTypeText = getJType(fieldType, outPackage, model);
				varLines.add("    private " + fieldTypeText + " " + fieldName + ";");
				methodLines.addAll(Arrays.asList(
						"",
						"    public " + fieldTypeText + " get" + methodSuffix + "() {",
						"        return " + fieldName + ";",
						"    }",
						"",
						"    public void set" + methodSuffix + "(" + fieldTypeText + " " + fieldName + ") {",
						"        this." + fieldName + " = " + fieldName + ";",
						"    }"
						));
			}
			List<String> classLines = new ArrayList<String>(Arrays.asList(
					"package " + outPackage + ";",
					""
					));
			String classHeaderLine = "public class " + className + " implements " + model.ref("java.io.Serializable") + " {";
			classLines.addAll(model.generateImports());
			classLines.add("");
			classLines.add(classHeaderLine);
			classLines.addAll(varLines);
			classLines.addAll(methodLines);
			classLines.add("}");
			TextUtils.writeFileLines(classLines, srcOutDir.openWriter(tupleFile));
		}
	}
	
	private static void generateTupleClasses(JavaData data, FileSaver srcOutDir, String outPackage) throws Exception {
		Set<Integer> tupleTypes = data.getTupleTypes();
		if (tupleTypes.size() > 0) {
			String outDir = outPackage.replace('.', '/');
			for (int tupleType : tupleTypes) {
				if (tupleType < 1)
					throw new IllegalStateException("Wrong tuple type: " + tupleType);
				String tupleFile = outDir + "/Tuple" + tupleType + "GWT.java";
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < tupleType; i++) {
					if (sb.length() > 0)
						sb.append(", ");
					sb.append('T').append(i+1);
				}
				List<String> classLines = new ArrayList<String>(Arrays.asList(
						"package " + outPackage + ";",
						"",
						"import java.io.Serializable;",
						"",
						"public class Tuple" + tupleType + "GWT <" + sb + "> implements Serializable {"
						));
				for (int i = 0; i < tupleType; i++) {
					classLines.add("    private T" + (i + 1) + " e" + (i + 1) + ";");
				}
				for (int i = 0; i < tupleType; i++) {
					classLines.addAll(Arrays.asList(
							"",
							"    public T" + (i + 1) + " getE" + (i + 1) + "() {",
							"        return e" + (i + 1) + ";",
							"    }",
							"",
							"    public void setE" + (i + 1) + "(T" + (i + 1) + " e" + (i + 1) + ") {",
							"        this.e" + (i + 1) + " = e" + (i + 1) + ";",
							"    }"
							));
				}
				classLines.add("}");
				TextUtils.writeFileLines(classLines, srcOutDir.openWriter(tupleFile));
			}
		}
	}

	private static String getJType(JavaType type, String outPackage, JavaImportHolder codeModel) throws Exception {
		KbBasicType kbt = type.getMainType();
		if (type.needClassGeneration()) {
			return codeModel.ref(outPackage + "." + type.getJavaClassName() + "GWT");
		} else if (kbt instanceof KbScalar) {
			return codeModel.ref(((KbScalar)kbt).getFullJavaStyleName());
		} else if (kbt instanceof KbList) {
			return codeModel.ref("java.util.List") + "<" + getJType(type.getInternalTypes().get(0), outPackage, codeModel) + ">";
		} else if (kbt instanceof KbMapping) {
			return codeModel.ref("java.util.LinkedHashMap")+ "<" + getJType(type.getInternalTypes().get(0), outPackage, codeModel) + "," +
					getJType(type.getInternalTypes().get(1), outPackage, codeModel) + ">";
		} else if (kbt instanceof KbTuple) {
			int paramCount = type.getInternalTypes().size();
			StringBuilder narrowParams = new StringBuilder();
			for (JavaType iType : type.getInternalTypes()) {
				if (narrowParams.length() > 0)
					narrowParams.append(", ");
				narrowParams.append(getJType(iType, outPackage, codeModel));
			}
			return codeModel.ref(outPackage + "." + "Tuple" + paramCount + "GWT") + "<" + narrowParams + ">";
		} else if (kbt instanceof KbUnspecifiedObject) {
			return codeModel.ref("java.lang.Object");
	    } else {
			throw new IllegalStateException("Unknown data type: " + kbt.getClass().getName());
		}
	}
}
