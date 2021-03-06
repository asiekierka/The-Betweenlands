package thebetweenlands.common.item.food;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IDecayFood;

public class ItemSapBall extends ItemBLFood implements IDecayFood {
	public ItemSapBall() {
		super(0, 0f, false);
		setAlwaysEdible();
	}

	@Override
	public int getDecayHealAmount(ItemStack stack) {
		return 2;
	}

	@Override
	public boolean canGetSickOf(ItemStack stack) {
		return false;
	}
}
