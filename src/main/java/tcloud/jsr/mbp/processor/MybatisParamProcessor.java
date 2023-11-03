package tcloud.jsr.mbp.processor;

import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import tcloud.jsr.mbp.annotation.UseActualParam;

/**
 * @Author Administrator
 * @Date 2023/11/2 17:06
 * @Description
 **/
@SupportedAnnotationTypes("tcloud.jsr.mbp.annotation.UseActualParam")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class MybatisParamProcessor extends AbstractProcessor {

    /**
     * JavacTrees提供了待处理的抽象语法树
     * TreeMaker中了一些操作抽象语法树节点的方法
     * Names提供了创建标识符的方法
     */
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        System.out.println("MybatisParamProcessor--init");
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("MybatisParamProcessor--process");
        roundEnv.getElementsAnnotatedWith(UseActualParam.class).stream()
                .map(element -> trees.getTree(element))
                .forEach(tree -> tree.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                        prependParamAnnotation(jcClassDecl);
                        super.visitClassDef(jcClassDecl);
                    }
                }));
        return true;
    }

    /**
     * 在DAO方法参数前边追加@Param注解
     */
    private void prependParamAnnotation(JCTree.JCClassDecl jcClassDecl) {
        System.out.println("MybatisParamProcessor--prependParamAnnotation");
        jcClassDecl.defs.stream()
                .filter(element -> element.getKind().equals(Tree.Kind.METHOD))
                .map(methodTree -> (JCTree.JCMethodDecl) methodTree)
                .forEach(methodTree -> {
                    methodTree.getParameters().forEach(parameter -> {
                        JCTree.JCAnnotation paramAnnotation = createParamAnnotation(parameter);
                        parameter.getModifiers().annotations.append(paramAnnotation);
                    });
                });
    }

    /**
     * 创建@Param注解对应的语法树对象
     */
    private JCTree.JCAnnotation createParamAnnotation(JCTree.JCVariableDecl parameter) {
        System.out.println("MybatisParamProcessor--createParamAnnotation");
        return treeMaker.Annotation(
                treeMaker.Ident(names.fromString("Param")),
                List.of(treeMaker.Assign(treeMaker.Ident(names.fromString("value")), treeMaker.Literal(parameter.name.toString()))));
    }

}