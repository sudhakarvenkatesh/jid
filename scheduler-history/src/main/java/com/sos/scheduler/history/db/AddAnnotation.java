package com.sos.scheduler.history.db;

import java.lang.reflect.Method;

import javax.persistence.Lob;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

public class AddAnnotation {

	public static void addPersonneNameAnnotationToMethod(String className,
			String methodName) throws Exception {

		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.getCtClass(className);
		CtMethod logMethodDescriptor = cc.getDeclaredMethod(methodName, null);

		ClassFile ccFile = cc.getClassFile();
		ConstPool constpool = ccFile.getConstPool();
		AnnotationsAttribute attr = new AnnotationsAttribute(constpool,
				AnnotationsAttribute.visibleTag);
		Annotation annot = new Annotation("Lob", constpool);
		attr.addAnnotation(annot);
		logMethodDescriptor.getMethodInfo().addAttribute(attr);

		Class dynamiqueBeanClass = cc.toClass();
		SchedulerHistoryLogDBItem logDBItem = (SchedulerHistoryLogDBItem) dynamiqueBeanClass
				.newInstance();

		try {

			Method logMessageMethod = logDBItem.getClass().getDeclaredMethod(
					methodName, null);
			Lob lob = (Lob) logMessageMethod.getAnnotation(Lob.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		try {
			AddAnnotation.addPersonneNameAnnotationToMethod(
					"com.sos.scheduler.history.db.SchedulerHistoryLogDBItem",
					"getLog");
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}