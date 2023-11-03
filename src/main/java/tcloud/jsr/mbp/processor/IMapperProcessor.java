package tcloud.jsr.mbp.processor;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import tcloud.jsr.mbp.annotation.IMapper;

/**
 * @Author Administrator
 * @Date 2023/11/2 20:30
 * @Description
 **/
@SupportedAnnotationTypes("tcloud.jsr.mbp.annotation.IMapper")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class IMapperProcessor extends AbstractProcessor {
    private static final String IMPORT_BASE_MAPPER_CLASS = "import com.baomidou.mybatisplus.core.mapper.BaseMapper;";
    private static final String IMPORT_BASE_MAPPER_ANNOTATION = "import org.apache.ibatis.annotations.Mapper;";
    private static final String BASE_MAPPER_ANNOTATION = "@Mapper";
    private static final String INTERFACE_TEMPLATE = "@Mapper";

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        System.out.println("MybatisParamProcessor--init");
        this.messager = processingEnv.getMessager();
    }

    /**
     * @param annotations the annotation types requested to be processed
     * @param roundEnv    environment for information about the current and prior round
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Elements elements = processingEnv.getElementUtils();
        // 获取使用了指定注解的元素
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(IMapper.class);
        for (Element typeElement : elementsAnnotatedWith) {
            // 被上面的注解的元素是否是一个类
            if (typeElement.getKind() == ElementKind.CLASS) {
                IMapper annotation = typeElement.getAnnotation(IMapper.class);
                String simpleTypeName = null;
                String qualifiedSuperClassName = null;
                try {
                    Class<?> clazz = annotation.value();
                    qualifiedSuperClassName = clazz.getCanonicalName();
                    simpleTypeName = clazz.getSimpleName();
                } catch (MirroredTypeException mte) {
                    DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                    TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                    qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
                    simpleTypeName = classTypeElement.getSimpleName().toString();
                }
                PackageElement packageOf = elements.getPackageOf(typeElement);
                String interfaceName = simpleTypeName + "Mapper";
                try {
                    generateInterface(packageOf.getQualifiedName()+".mapper", interfaceName, qualifiedSuperClassName, simpleTypeName);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
        return true;
    }

    private void generateInterface(String packageName, String interfaceName,String fullname, String simpleName) throws IOException {
        JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(packageName + "." + interfaceName);
        try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
            writer.println("package " + packageName + ";");
            writer.println(IMPORT_BASE_MAPPER_ANNOTATION);
            writer.println(IMPORT_BASE_MAPPER_CLASS);
            writer.println("import " + fullname + ";");
            writer.println(BASE_MAPPER_ANNOTATION);
            writer.println("public interface " + interfaceName + " extends BaseMapper<" + simpleName + "> {");
            writer.println();
            writer.println("}");
        }
    }
}