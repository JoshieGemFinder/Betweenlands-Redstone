package com.joshiegemfinder.betweenlandsredstone.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;

public abstract class ClassTransformer implements IClassTransformer {

	private final String className;
	private final String classNameObfuscated;
	private boolean isObfuscated = false;
	
	public ClassTransformer(String classNameMCP, String classNameObfuscated) {
		this.className = classNameMCP;
		this.classNameObfuscated = classNameObfuscated;
	}
	
	protected boolean isObfuscated() {
		return this.isObfuscated;
	}
	
	protected String getMappedName(String obfuscated, String deobfuscated) {
		if(this.isObfuscated())
			return obfuscated;
		return deobfuscated;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] classBytes) {
		if(name.equals(className)) {
			this.isObfuscated = false;
		} else if(name.equals(classNameObfuscated)) {
			this.isObfuscated = true;
		} else {
			return classBytes;
		}
		

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(classBytes);
		classReader.accept(classNode, 0);
		final boolean successful = this.transformClass(classNode, name, transformedName, classBytes);
		
//		System.out.println("Transform successful? " + String.valueOf(successful));
		if(!successful) {
			return classBytes;
		}
		
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		classNode.accept(classWriter);
		return classWriter.toByteArray();
	}
	
	public abstract boolean transformClass(ClassNode classNode, String name, String transformedName, byte[] classBytes);
}
