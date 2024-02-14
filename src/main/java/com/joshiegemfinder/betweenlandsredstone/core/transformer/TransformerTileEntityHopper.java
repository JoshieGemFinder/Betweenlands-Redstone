package com.joshiegemfinder.betweenlandsredstone.core.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.joshiegemfinder.betweenlandsredstone.core.ClassTransformer;

public class TransformerTileEntityHopper extends ClassTransformer {

	public TransformerTileEntityHopper() {
		super("net.minecraft.tileentity.TileEntityHopper", "avw");
	}

	@Override
	public boolean transformClass(ClassNode classNode, String name, String transformedName, byte[] classBytes) {
//		System.out.println("Transforming TileEntityHopper");
		for(MethodNode method : classNode.methods) {
//			System.out.println(new StringBuilder().append("Method: ").append(method.name).append(method.desc).append("  Equals: ").append(this.getMappedName("b", "getInventoryAtPosition")).append(this.getMappedName("(Lamu;DDD)Ltv;", "Lnet/minecraft/world/World;DDD)Lnet/minecraft/inventory/IInventory;")).toString());
			if(method.name.equals(this.getMappedName("b", "getInventoryAtPosition")) && method.desc.equals(this.getMappedName("(Lamu;DDD)Ltv;", "Lnet/minecraft/world/World;DDD)Lnet/minecraft/inventory/IInventory;"))) {

				boolean foundGetBlock = false;
				for(int i = 0; i < method.instructions.size(); i++) {
					
					AbstractInsnNode abstractNode = method.instructions.get(i);
					
					if(!foundGetBlock && abstractNode instanceof MethodInsnNode) {
						MethodInsnNode methodCallNode = (MethodInsnNode)abstractNode;
//						System.out.println("Found a method at i " + String.valueOf(i));
						
						if(
							methodCallNode.name.equals(this.getMappedName("u", "getBlock")) &&
							methodCallNode.desc.equals(this.getMappedName("()Laow;", "()Lnet/minecraft/block/Block;")) &&
							methodCallNode.owner.equals(this.getMappedName("awt", "net/minecraft/block/state/IBlockState"))
						) {
//							System.out.println("Found a valid method at i " + String.valueOf(i));
							foundGetBlock = true;
						}
					} else if(foundGetBlock && abstractNode instanceof VarInsnNode && abstractNode.getOpcode() == Opcodes.ASTORE) {
						VarInsnNode storeNode = ((VarInsnNode)abstractNode);

//						System.out.println("Found a target astore at i " + String.valueOf(i));
						
						if(storeNode.var != 13) {
							System.out.println("Huih? Conflict with other coremod?? Expected astore 13, got astore " + String.valueOf(storeNode.var));
						}
//						System.out.println("Adding nodes");
						
						//this is the node we're inserting stuff after
						InsnList insertAfter = new InsnList();

						LabelNode postIfLabel = new LabelNode();
						
						insertAfter.add(new VarInsnNode(Opcodes.ALOAD, storeNode.var));
						insertAfter.add(new TypeInsnNode(Opcodes.INSTANCEOF, "com/joshiegemfinder/betweenlandsredstone/util/ISidedInventoryProvider"));
						insertAfter.add(new JumpInsnNode(Opcodes.IFEQ, postIfLabel));
						insertAfter.add(new VarInsnNode(Opcodes.ALOAD, storeNode.var));
						insertAfter.add(new TypeInsnNode(Opcodes.CHECKCAST, "com/joshiegemfinder/betweenlandsredstone/util/ISidedInventoryProvider"));
						insertAfter.add(new VarInsnNode(Opcodes.ALOAD, 0)); // where is the aload_0 node?
						insertAfter.add(new VarInsnNode(Opcodes.ALOAD, 11));
						insertAfter.add(new VarInsnNode(Opcodes.ALOAD, 12));
						insertAfter.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "com/joshiegemfinder/betweenlandsredstone/util/ISidedInventoryProvider", "getContainer", this.getMappedName("(Lamu;Let;Lawt;)Lun;", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/inventory/ISidedInventory;"), true));
						insertAfter.add(new InsnNode(Opcodes.ARETURN));
						insertAfter.add(postIfLabel);
						
						method.instructions.insert(storeNode, insertAfter);
						
						return true;
					}
					
				}
				
			}
		}
		
		return false;
	}

}
