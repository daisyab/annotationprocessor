/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package processor;

import annotation.Action;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 *
 * @author guestu
 */

@SupportedAnnotationTypes("annotation.Action")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ActionProcessor extends AbstractProcessor{
    
    public ActionProcessor(){
        
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv){
        for(Element e : roundEnv.getElementsAnnotatedWith(Action.class)){
            if(e.getKind() != ElementKind.FIELD){
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.WARNING, "Not a field", e);
                continue;
            }
            String name = capitalize(e.getSimpleName().toString());
            TypeElement clazz = (TypeElement) e.getEnclosingElement();
            try{
                JavaFileObject jfo = processingEnv.getFiler().
                        createSourceFile(clazz.getQualifiedName() + "2");
                processingEnv.getMessager().
                        printMessage(Diagnostic.Kind.NOTE, "Creating " + jfo.toUri());
                Writer writer = jfo.openWriter();
                try{
                    PrintWriter printwriter = new PrintWriter(writer);
                    printwriter.println("package " +
                            clazz.getEnclosingElement() + ";");  //prints package com.main.java.gsoc;
                    printwriter.println();
                    printwriter.println("public abstract class " +
                            clazz.getSimpleName() + "2{");
                    printwriter.println("   protected " + clazz.getSimpleName() +
                            "2(){");
                    printwriter.println("   }");
                    
                    TypeMirror type = e.asType();
                    printwriter.println("   protected final void action" + name +
                            "(" + type +" value){");
                    printwriter.println(        "System.out.println(value);");
                    printwriter.println(    "}");
                    printwriter.println("}");
                    printwriter.flush();
                }finally{
                   writer.close();
                }
            }catch (IOException ex){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, 
                        ex.toString());
            }
        }
        return true;        
    }

    private String capitalize(String name) {
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }
}
