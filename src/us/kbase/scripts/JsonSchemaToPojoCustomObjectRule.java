package us.kbase.scripts;

import static com.googlecode.jsonschema2pojo.rules.PrimitiveTypes.isPrimitive;
import static com.googlecode.jsonschema2pojo.rules.PrimitiveTypes.primitiveType;
import static org.apache.commons.lang.StringUtils.capitalize;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonschema2pojo.Schema;
import com.googlecode.jsonschema2pojo.SchemaMapper;
import com.googlecode.jsonschema2pojo.exception.ClassAlreadyExistsException;
import com.googlecode.jsonschema2pojo.rules.Rule;
import com.googlecode.jsonschema2pojo.rules.RuleFactory;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class JsonSchemaToPojoCustomObjectRule implements Rule<JPackage, JType> {
	
	private final RuleFactory ruleFactory;
	
	public JsonSchemaToPojoCustomObjectRule(RuleFactory parent) {
		this.ruleFactory = parent;
	}

    @Override
    public JType apply(String nodeName, JsonNode node, JPackage _package, Schema schema) {

        JType superType = getSuperType(nodeName, node, _package, schema);

        if (superType.isPrimitive() || isFinal(superType)) {
            return superType;
        }

        JDefinedClass jclass;
        try {
            jclass = createClass(nodeName, node, _package);
        } catch (ClassAlreadyExistsException e) {
            return e.getExistingClass();
        }

        jclass._extends((JClass) superType);

        schema.setJavaTypeIfEmpty(jclass);
        addGeneratedAnnotation(jclass);

        if (node.has("title")) {
            ruleFactory.getTitleRule().apply(nodeName, node.get("title"), jclass, schema);
        }

        if (node.has("description")) {
            ruleFactory.getDescriptionRule().apply(nodeName, node.get("description"), jclass, schema);
        }

        if (node.has("properties")) {
            ruleFactory.getPropertiesRule().apply(nodeName, node.get("properties"), jclass, schema);
        }

        if (ruleFactory.getGenerationConfig().isIncludeToString()) {
            addToString(jclass);
        }

        if (ruleFactory.getGenerationConfig().isIncludeHashcodeAndEquals()) {
            addHashCode(jclass);
            addEquals(jclass);
        }

        if (node.has("javaInterfaces")) {
            addInterfaces(jclass, node.get("javaInterfaces"));
        }

        ruleFactory.getAdditionalPropertiesRule().apply(nodeName, node.get("additionalProperties"), jclass, schema);

        return jclass;

    }

    private JDefinedClass createClass(String nodeName, JsonNode node, JPackage _package) throws ClassAlreadyExistsException {

        JDefinedClass newType;

        try {
            if (node.has("javaType")) {
                String fqn = node.get("javaType").asText();

                if (isPrimitive(fqn, _package.owner())) {
                    throw new ClassAlreadyExistsException(primitiveType(fqn, _package.owner()));
                }

                try {
                	if (fqn.endsWith(".UObject")) {
                		throw new ClassAlreadyExistsException(_package.owner().ref(fqn));
                	}
                	if (node.has("javaTypeParams")) {
                        JClass ret = loadParameterizedType(node, _package.owner());
                        throw new ClassAlreadyExistsException(ret);
                	}
                    //Class<?> existingClass = Thread.currentThread().getContextClassLoader().loadClass(fqn);
                    //System.out.println("JsonSchemaToPojoCustomObjectRule: class [" + fqn + "] already exists");
                    //throw new ClassAlreadyExistsException(_package.owner().ref(existingClass));
                	throw new ClassNotFoundException(fqn);
                } catch (ClassNotFoundException e) {
                    newType = _package.owner()._class(fqn);
                }
            } else {
                newType = _package._class(getClassName(nodeName, _package));
            }
        } catch (JClassAlreadyExistsException e) {
            throw new ClassAlreadyExistsException(e.getExistingClass());
        }

        ruleFactory.getAnnotator().propertyInclusion(newType, node);

        return newType;

    }

	private JClass loadParameterizedType(JsonNode node, JCodeModel codeModel) {
		String className = node.get("javaType").asText();                		
		JClass ret = codeModel.ref(className);
		if (node.has("javaTypeParams")) {
			List<JClass> paramTypes = new ArrayList<JClass>();
			JsonNode paramNode = node.get("javaTypeParams");
			if (paramNode.isArray()) {
				Iterator<JsonNode> it = paramNode.elements();
				while (it.hasNext())
					paramTypes.add(loadParameterizedType(it.next(), codeModel));
			} else {
				paramTypes.add(loadParameterizedType(paramNode, codeModel));
			}
			ret = ret.narrow(paramTypes);
		}
		return ret;
	}

    private boolean isFinal(JType superType) {
        try {
            Class<?> javaClass = Class.forName(superType.fullName());
            return Modifier.isFinal(javaClass.getModifiers());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private JType getSuperType(String nodeName, JsonNode node, JClassContainer jClassContainer, Schema schema) {
        JType superType = jClassContainer.owner().ref(Object.class);
        if (node.has("extends")) {
            superType = ruleFactory.getSchemaRule().apply(nodeName + "Parent", node.get("extends"), jClassContainer, schema);
        }
        return superType;
    }

    private void addGeneratedAnnotation(JDefinedClass jclass) {
        JAnnotationUse generated = jclass.annotate(Generated.class);
        generated.param("value", SchemaMapper.class.getPackage().getName());
    }

    private void addToString(JDefinedClass jclass) {
        JMethod toString = jclass.method(JMod.PUBLIC, String.class, "toString");

        JBlock body = toString.body();
        JInvocation reflectionToString = jclass.owner().ref(ToStringBuilder.class).staticInvoke("reflectionToString");
        reflectionToString.arg(JExpr._this());
        body._return(reflectionToString);

        toString.annotate(Override.class);
    }

    private void addHashCode(JDefinedClass jclass) {
        JMethod hashCode = jclass.method(JMod.PUBLIC, int.class, "hashCode");

        JBlock body = hashCode.body();
        JInvocation reflectionHashCode = jclass.owner().ref(HashCodeBuilder.class).staticInvoke("reflectionHashCode");
        reflectionHashCode.arg(JExpr._this());
        body._return(reflectionHashCode);

        hashCode.annotate(Override.class);
    }

    private void addEquals(JDefinedClass jclass) {
        JMethod equals = jclass.method(JMod.PUBLIC, boolean.class, "equals");
        JVar otherObject = equals.param(Object.class, "other");

        JBlock body = equals.body();
        JInvocation reflectionEquals = jclass.owner().ref(EqualsBuilder.class).staticInvoke("reflectionEquals");
        reflectionEquals.arg(JExpr._this());
        reflectionEquals.arg(otherObject);
        body._return(reflectionEquals);

        equals.annotate(Override.class);
    }

    private void addInterfaces(JDefinedClass jclass, JsonNode javaInterfaces) {
        for (JsonNode i : javaInterfaces) {
            jclass._implements(jclass.owner().ref(i.asText()));
        }
    }

    private String getClassName(String nodeName, JPackage _package) {
        String className = ruleFactory.getNameHelper().replaceIllegalCharacters(capitalize(nodeName));
        String normalizedName = ruleFactory.getNameHelper().normalizeName(className);
        return makeUnique(normalizedName, _package);
    }

    private String makeUnique(String className, JPackage _package) {
        try {
            JDefinedClass _class = _package._class(className);
            _package.remove(_class);
            return className;
        } catch (JClassAlreadyExistsException e) {
            return makeUnique(className + "_", _package);
        }
    }
}
