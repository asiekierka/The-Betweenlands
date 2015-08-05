package thebetweenlands.aspect;

import net.minecraft.nbt.NBTTagCompound;

public class AspectAzuwynn implements IAspect {
	public String getName() {
		return "Azuwynn";
	}

	public String getType() {
		return "Muscle";
	}

	public String getDescription() {
		return "Has effect on the muscles, could either result in more damage, speed or maybe rapid fire and all stuff in that regard.";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
