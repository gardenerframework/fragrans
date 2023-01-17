package io.gardenerframework.fragrans.sugar.lang.type.apt;

import io.gardenerframework.fragrans.sugar.lang.type.annotation.Uninherit;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/9/16 4:06 下午
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.gardenerframework.fragrans.sugar.lang.type.annotation.Uninherit")
public class UninheritAnnotationProcessor extends AbstractProcessor {
    /**
     * 语法树
     */
    private JavacTrees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Uninherit.class)
                .forEach(clazz -> {
                    List<String> targets = getTargets(clazz);
                    trees.getTree(clazz).accept(new TreeTranslator() {
                        @Override
                        public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                            removeUninheritTargets(targets, jcClassDecl);
                            super.visitClassDef(jcClassDecl);
                        }
                    });
                });
        return true;
    }

    private void removeUninheritTargets(List<String> targets, JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCExpression extendsClause = jcClassDecl.getExtendsClause();
        if (extendsClause != null && targets.contains(escapeTypeParameter(extendsClause))) {
            jcClassDecl.extending = null;
        }
        com.sun.tools.javac.util.List<JCTree.JCExpression> implementsClause = jcClassDecl.getImplementsClause();
        if (implementsClause != null) {
            List<JCTree.JCExpression> implementz = new LinkedList<>();
            implementsClause.forEach(
                    implement -> {
                        if (!targets.contains(escapeTypeParameter(implement))) {
                            implementz.add(implement);
                        }
                    }
            );
            jcClassDecl.implementing = com.sun.tools.javac.util.List.from(implementz.toArray(new JCTree.JCExpression[]{}));
        }
    }

    private String escapeTypeParameter(JCTree.JCExpression jcExpression) {
        if (jcExpression instanceof JCTree.JCTypeApply) {
            return ((JCTree.JCTypeApply) jcExpression).clazz.type.toString();
        } else {
            return jcExpression.type.toString();
        }
    }

    private List<String> getTargets(Element element) {
        Uninherit annotation = element.getAnnotation(Uninherit.class);
        List<? extends TypeMirror> targets = null;
        try {
            annotation.value();
        } catch (MirroredTypesException exception) {
            targets = exception.getTypeMirrors();
        }
        return Objects.requireNonNull(targets).stream().map(
                TypeMirror::toString

        ).collect(Collectors.toList());
    }
}
