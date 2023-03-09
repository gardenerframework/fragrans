package io.gardenerframework.fragrans.sugar.trait.apt;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;
import io.gardenerframework.fragrans.sugar.trait.annotation.TraitNamespace;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.*;

/**
 * @author zhanghan30
 * @date 2022/8/14 2:01 上午
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.gardenerframework.fragrans.sugar.trait.annotation.Trait")
public class TraitAnnotationProcessor extends AbstractProcessor {
    private final Map<String, Element> namespaceClasses = new HashMap<>();
    /**
     * 语法树
     */
    private JavacTrees trees;
    /**
     * 树创建工具
     */
    private TreeMaker treeMaker;
    /**
     * 名称工具
     */
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //扫描所有命名空间
        scanTraitNamespaces(roundEnv);
        //处理注解
        processTraitAnnotation(roundEnv);
        return true;
    }

    /**
     * 获取素有命名空间
     *
     * @param roundEnv 编译环境
     */
    private void scanTraitNamespaces(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(TraitNamespace.class).forEach(
                clazz -> namespaceClasses.put(
                        clazz.asType().toString(),
                        clazz
                )
        );
    }

    /**
     * 处理注解
     *
     * @param roundEnv 环境
     */
    private void processTraitAnnotation(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Trait.class).forEach(
                clazz -> {
                    Trait annotation = clazz.getAnnotation(Trait.class);
                    TypeMirror namespace = null;
                    try {
                        //这里必然会抛出异常
                        annotation.namespace();
                    } catch (MirroredTypeException exception) {
                        namespace = exception.getTypeMirror();
                    }
                    if (!Trait.class.getName().equals(namespace.toString())) {
                        addTraitToNamespace(namespace, clazz);
                    } else {
                        turnTraitClassToInterface(clazz);
                    }
                }
        );
    }

    /**
     * 将指定trait类放入namespace
     *
     * @param namespace 命名空间
     * @param clazz     指定的trait类
     */
    private void addTraitToNamespace(TypeMirror namespace, Element clazz) {
        //获取命名空间对应的元素
        Element namespaceElement = namespaceClasses.get(namespace.toString());
        if (namespaceElement == null) {
            throw new IllegalArgumentException("no namespace class " + namespace + " found. define such class with @TraitNamespace first");
        }
        //获得编译语法树
        JCTree tree = trees.getTree(namespaceElement);
        tree.accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                //当前正在处理的类是命名空间类
                if (jcClassDecl.getSimpleName().equals(names.fromString(namespaceElement.getSimpleName().toString()))) {
                    jcClassDecl.defs = jcClassDecl.defs.append(createInnerInterface(clazz));
                }
                super.visitClassDef(jcClassDecl);
            }
        });
    }

    /**
     * 将trait类转成interface
     *
     * @param clazz 类
     */
    private void turnTraitClassToInterface(Element clazz) {
        trees.getTree(clazz).accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                java.util.List<JCTree.JCVariableDecl> variables = new LinkedList<>();
                java.util.List<JCTree.JCMethodDecl> methods = new LinkedList<>();
                for (JCTree node : jcClassDecl.defs) {
                    if (Tree.Kind.METHOD.equals(node.getKind())) {
                        methods.add((JCTree.JCMethodDecl) node);
                    }
                    if (Tree.Kind.VARIABLE.equals(node.getKind())) {
                        //这是一个变量，加入到清单中
                        variables.add((JCTree.JCVariableDecl) node);
                    }
                }
                //改成接口
                jcClassDecl.mods = treeMaker.Modifiers(Flags.PUBLIC | Flags.INTERFACE);
                //原来的树不让改，新建一个
                java.util.List<JCTree> temp = new ArrayList<>(jcClassDecl.defs);
                //去掉所有方法
                for (JCTree.JCMethodDecl method : methods) {
                    temp.remove(method);
                }
                //去掉所有变量定义和添加getter setter
                for (JCTree.JCVariableDecl variable : variables) {
                    temp.remove(variable);
                    temp.add(createGetter(variable));
                    temp.add(createSetter(variable));
                }
                jcClassDecl.defs = List.from(temp);
                super.visitClassDef(jcClassDecl);
            }
        });
    }

    /**
     * @param clazz 要生成的类
     * @return 类声明
     */
    private JCTree.JCClassDecl createInnerInterface(Element clazz) {
        //获取所有模板范型
        java.util.List<JCTree.JCTypeParameter> typeParameters = new ArrayList<>();
        trees.getTree(clazz).accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                super.visitClassDef(jcClassDecl);
                typeParameters.addAll(jcClassDecl.getTypeParameters());
            }
        });
        //获得接口声明
        JCTree.JCClassDecl interfaceDecl = treeMaker.ClassDef(
                treeMaker.Modifiers(Flags.PUBLIC | Flags.INTERFACE),
                names.fromString(clazz.getSimpleName().toString()),
                List.from(typeParameters),
                null,
                List.nil(),
                List.nil()
        );
        //遍历trait类的所有变量
        trees.getTree(clazz).accept(new TreeTranslator() {
            @Override
            public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
                super.visitVarDef(jcVariableDecl);
                //向接口中添加getter & setter
                interfaceDecl.defs = interfaceDecl.defs.prepend(createGetter(jcVariableDecl));
                interfaceDecl.defs = interfaceDecl.defs.prepend(createSetter(jcVariableDecl));
            }
        });
        return interfaceDecl;
    }

    /**
     * 生成getter方法
     *
     * @param jcVariableDecl 变量定义
     * @return getter方法
     */
    private JCTree.JCMethodDecl createGetter(JCTree.JCVariableDecl jcVariableDecl) {
        //默认前缀是get
        String prefix = "get";
        JCTree.JCExpression vartype = jcVariableDecl.vartype;
        //boolean类型前缀是is
        if (vartype.getTree().type.isPrimitive() && "boolean".equals(vartype.getTree().type.toString())) {
            prefix = "is";
        }
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString(buildMethodName(prefix, jcVariableDecl.getName().toString())),
                vartype,
                List.nil(), List.nil(), List.nil(), null, null

        );
    }

    /**
     * 生成setter方法
     *
     * @param jcVariableDecl 变量定义
     * @return setter方法
     */
    private JCTree.JCMethodDecl createSetter(JCTree.JCVariableDecl jcVariableDecl) {
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString(buildMethodName("set", jcVariableDecl.getName().toString())),
                null,
                List.nil(),
                List.of(
                        treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER),
                                jcVariableDecl.getName(),
                                jcVariableDecl.vartype,
                                null)
                ),
                List.nil(),
                null,
                null
        );
    }

    /**
     * 返回getter setter的名字
     *
     * @param prefix  前缀
     * @param varName 变量名
     * @return 方法名
     */
    private String buildMethodName(String prefix, String varName) {
        return prefix + varName.substring(0, 1).toUpperCase() + varName.substring(1);
    }
}
