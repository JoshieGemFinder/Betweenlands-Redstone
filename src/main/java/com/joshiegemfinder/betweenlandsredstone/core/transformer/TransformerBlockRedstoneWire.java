package com.joshiegemfinder.betweenlandsredstone.core.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.joshiegemfinder.betweenlandsredstone.core.ClassTransformer;

public class TransformerBlockRedstoneWire extends ClassTransformer {

	public TransformerBlockRedstoneWire() {
		super("net.minecraft.block.BlockRedstoneWire", "atf");
	}

	@Override
	public boolean transformClass(ClassNode classNode, String name, String transformedName, byte[] classBytes) {
		
		for(MethodNode method : classNode.methods) {
			if(method.name.equals(this.getMappedName("a", "calculateCurrentChanges")) && method.desc.equals(this.getMappedName("(Lamu;Let;Let;Lawt;)Lawt;", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"))) {
				
				for(int i = 0; i < method.instructions.size(); i++) {
					
					AbstractInsnNode abstractNode = method.instructions.get(i);
					
					if(abstractNode instanceof MethodInsnNode) {
						MethodInsnNode methodCallNode = (MethodInsnNode)abstractNode;
						
						if(
							methodCallNode.name.equals(this.getMappedName("z", "isBlockIndirectlyGettingPowered")) &&
							methodCallNode.desc.equals(this.getMappedName("(Let;)I", "Lnet/minecraft/util/math/BlockPos;)I")) &&
							methodCallNode.owner.equals(this.getMappedName("amu", "net/minecraft/world/World"))
						) {
							//luckily, all the `aload_<n>`s line up perfectly for it to be as simple as this because of invokestatic
							method.instructions.set(methodCallNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/joshiegemfinder/betweenlandsredstone/util/Connector", "isBlockIndirectlyGettingPoweredRedstoneWire", this.getMappedName("(Lamu;Let;)I", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)I"), false));
							return true;
						}
					}
					
				}
				
			}
		}
		
		return false;
	}

}
