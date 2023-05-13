package com.mactso.harderfarther.block.properties;

import net.minecraft.util.StringIdentifiable;

public enum GrimGateType implements StringIdentifiable{
	
	   FLOOR("floor"),
	   DOOR("door");

	   private final String name;

	   private GrimGateType(String s) {
	      this.name = s;
	   }

	   public String toString() {
	      return this.asString();
	   }

	   public String asString() {
	      return this.name;
	   }
}
