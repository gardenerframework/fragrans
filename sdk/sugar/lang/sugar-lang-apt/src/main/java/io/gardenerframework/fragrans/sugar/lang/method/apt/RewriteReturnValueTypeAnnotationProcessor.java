package io.gardenerframework.fragrans.sugar.lang.method.apt;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import io.gardenerframework.fragrans.sugar.lang.method.annotation.KeepReturnValueType;
import io.gardenerframework.fragrans.sugar.lang.method.annotation.RewriteReturnValueType;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author zhanghan30
 * @date 2022/9/14 4:57 下午
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.gardenerframework.fragrans.sugar.lang.method.annotation.RewriteReturnValueType")
public class RewriteReturnValueTypeAnnotationProcessor extends AbstractProcessor {
    /**
     * 语法树
     */
    private JavacTrees trees;
    /**
     * 上下文
     */
    private Context context;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
        this.context = ((JavacProcessingEnvironment) processingEnv).getContext();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processClassLevel(roundEnv);
        processMethodLevel(roundEnv);
        return true;
    }

    private void processClassLevel(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(RewriteReturnValueType.class).forEach(
                element -> {
                    if (ElementKind.CLASS.equals(element.getKind())) {
                        rewriteMethodResultType(element);
                    }
                }
        );
    }

    private void processMethodLevel(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(RewriteReturnValueType.class).forEach(
                element -> {
                    if (ElementKind.METHOD.equals(element.getKind())) {
                        rewriteMethodResultType(element);
                    }
                }
        );
    }

    private void rewriteMethodResultType(Element element) {
        JCTree tree = trees.getTree(element);
        if (tree instanceof JCTree.JCClassDecl) {
            //修改成这样，目前visitMethodDef会遍历内部类的方法，于是需要明确找到当前类声明的所有方法
            for (JCTree def : ((JCTree.JCClassDecl) tree).defs) {
                if (def instanceof JCTree.JCMethodDecl) {
                    def.accept(translateJCMethodDecl(element));
                }
            }
        } else {
            tree.accept(translateJCMethodDecl(element));
        }
    }

    private TreeTranslator translateJCMethodDecl(Element element) {
        return new TreeTranslator() {
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                if (jcMethodDecl.restype != null && (jcMethodDecl.getModifiers().flags & Flags.PUBLIC) != 0 && !keepReturnValue(jcMethodDecl)) {
                    //带有返回值类型，也就是非构造方法
                    //同时必须是public方法
                    //同时必须没有带有KeepReturnValue注解
                    JavacParser parser = ParserFactory.instance(context).newParser(getRewriteTarget(element), false, true, false);
                    jcMethodDecl.restype = parser.parseType();
                    removeOverrideAnnotation(jcMethodDecl);
                }
                super.visitMethodDef(jcMethodDecl);
            }
        };
    }

    private boolean keepReturnValue(JCTree.JCMethodDecl jcMethodDecl) {
        com.sun.tools.javac.util.List<JCTree.JCAnnotation> annotations = jcMethodDecl.getModifiers().getAnnotations();
        if (annotations != null && !annotations.isEmpty()) {
            //注解不为空
            for (JCTree.JCAnnotation annotation : annotations) {
                JCTree annotationType = annotation.getAnnotationType();
                if (annotationType instanceof JCTree.JCIdent) {
                    Symbol sym = ((JCTree.JCIdent) annotationType).sym;
                    if (sym instanceof Symbol.ClassSymbol && KeepReturnValueType.class.getName().equals(((Symbol.ClassSymbol) sym).fullname.toString())) {
                        //注解的符号是类型符号并且全名和KeepReturnValueType注解一致
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void removeOverrideAnnotation(JCTree.JCMethodDecl jcMethodDecl) {
        JCTree.JCModifiers modifiers = jcMethodDecl.getModifiers();
        if (modifiers != null) {
            com.sun.tools.javac.util.List<JCTree.JCAnnotation> original = modifiers.getAnnotations();
            List<JCTree.JCAnnotation> annotations = new LinkedList<>();
            if (original != null) {
                original.forEach(
                        annotation -> {
                            if (annotation.type == null) {
                                //处理类型为空的情况
                                //实际编译报错过
                                JCTree annotationTreeType = annotation.getAnnotationType();
                                if (!(annotation.type == null &&
                                        annotationTreeType instanceof JCTree.JCIdent &&
                                        Override.class.getSimpleName().equals(
                                                ((JCTree.JCIdent) annotationTreeType).getName()
                                        )) || !Override.class.getName().equals(annotation.type.toString())) {
                                    annotations.add(annotation);
                                }
                            }
                        }
                );
            }
            jcMethodDecl.mods.annotations = com.sun.tools.javac.util.List.from(annotations.toArray(new JCTree.JCAnnotation[]{}));
        }
    }

    private String getRewriteTarget(Element element) {
        RewriteReturnValueType annotation = element.getAnnotation(RewriteReturnValueType.class);
        TypeMirror rewriteTarget = null;
        try {
            annotation.value();
        } catch (MirroredTypeException exception) {
            rewriteTarget = exception.getTypeMirror();
        }
        return Objects.requireNonNull(rewriteTarget).toString();
    }
}
